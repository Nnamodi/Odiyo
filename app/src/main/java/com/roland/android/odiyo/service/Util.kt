package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Constants.DATE
import com.roland.android.odiyo.service.Constants.MB_DIVISOR
import com.roland.android.odiyo.service.Constants.MB_FORMAT
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.states.MediaUiState
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Util {
	lateinit var notificationManager: OdiyoNotificationManager

	var mediaSession: MediaSession? = null

	val mediaUiState = MutableStateFlow(MediaUiState())

	val mediaItemsUiState = MutableStateFlow(MediaItemsUiState())

	val nowPlayingUiState = MutableStateFlow(NowPlayingUiState())

	val nowPlayingMetadata = MutableStateFlow<MediaMetadata?>(null)

	val playingState = MutableStateFlow(false)

	val readStoragePermissionGranted = MutableStateFlow(false)

	// mutable list of MediaItems for populating the Player
	val mediaItems = MutableStateFlow<MutableList<MediaItem>>(mutableListOf())

	// mutable list of Music items on queue - will be gotten from mediaItems
	val songsOnQueue = MutableStateFlow<MutableList<Music>>(mutableListOf())

	val Long.time: String
		get() {
			val hour = ((this / 1000) / 60) / 60
			val minute = ((this / 1000) / 60) % 60
			val second = (this / 1000) % 60
			val hours = if (hour > 0) "$hour:" else ""
			val minutes = if (minute < 10) "0$minute:" else "$minute:"
			val seconds = if (second < 10) "0$second" else "$second"
			return "$hours$minutes$seconds"
		}

	val Long.date: String
		get() = SimpleDateFormat(DATE, Locale.getDefault()).format(this * 1000)

	val Int.toMb: String
		get() = DecimalFormat(MB_FORMAT).format(this / MB_DIVISOR)

	val Uri.toMediaItem: MediaItem
		get() = MediaItem.Builder().setUri(this).build()

	fun MediaItem.getBitmap(context: Context): Bitmap {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
		return localConfiguration?.uri?.getMediaArt(context) ?: defaultArt
	}

	fun Music.getBitmap(context: Context): Bitmap {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
		return uri.getMediaArt(context) ?: defaultArt
	}

	fun Album.getBitmap(context: Context): Bitmap {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_album_art)
		return uri.getMediaArt(context) ?: defaultArt
	}

	fun Artist.getBitmap(context: Context): Bitmap {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_artist_art)
		return uri.getMediaArt(context) ?: defaultArt
	}

	fun Playlist.getBitmap(context: Context): Bitmap {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_playlist_art)
		return if (numOfSongs() > 0) {
			songs[numOfSongs() - 1].getMediaArt(context) ?: defaultArt
		} else defaultArt
	}

	private fun Uri.getMediaArt(context: Context): Bitmap? {
		val retriever = MediaMetadataRetriever()
		try {
			retriever.setDataSource(context, this)
		} catch (e: RuntimeException) {
			Log.e("MediaPathInfo", "Can't retrieve media file", e)
		}
		val bytes = retriever.embeddedPicture
		retriever.release()
		return bytes?.size?.let { BitmapFactory.decodeByteArray(bytes, 0, it) }
	}

	// an intent to launch UI from player notification.
	val Context.pendingIntent: PendingIntent
		get() = PendingIntent.getActivity(
				this,
				0,
				MainActivity.newInstance(this),
				PendingIntent.FLAG_IMMUTABLE
			)

	val NOTHING_PLAYING = MediaItem.Builder().setUri("null".toUri()).build()
}