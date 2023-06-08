package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Constants.DATE
import com.roland.android.odiyo.service.Constants.MB_DIVISOR
import com.roland.android.odiyo.service.Constants.MB_FORMAT
import com.roland.android.odiyo.service.Constants.TIME
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
object Util {
	val audioAttribute = AudioAttributes.Builder()
		.setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
		.setUsage(C.USAGE_MEDIA)
		.build()

	var mediaSession: MediaSession? = null

	val nowPlaying = MutableStateFlow<MediaItem?>(null)

	val nowPlayingMetadata = MutableStateFlow<MediaMetadata?>(null)

	val playingState = MutableStateFlow(false)

	val deviceMuteState = MutableStateFlow(false)

	val shuffleModeState = MutableStateFlow(false)

	val progress = MutableStateFlow(0L)

	val currentMediaIndex = MutableStateFlow(0)

	// mutable list of MediaItems for populating the Player
	val mediaItems = MutableStateFlow<MutableList<MediaItem>>(mutableListOf())

	// mutable list of Music items on queue - will be gotten from mediaItems
	val songsOnQueue = MutableStateFlow<MutableList<Music>>(mutableListOf())

	val Long.time: String
		get() = LocalDateTime.ofInstant(
			Instant.ofEpochMilli(this),
			ZoneId.systemDefault()
		).format(
			DateTimeFormatter.ofPattern(
				TIME,
				Locale.getDefault()
			)
		)

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
		return if (numSongs > 0) {
			songs[numSongs - 1].getMediaArt(context) ?: defaultArt
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
		return bytes?.size?.let { BitmapFactory.decodeByteArray(bytes, 0, it) }
	}

	// an intent to launch UI from player notification.
	@ExperimentalAnimationApi
	@ExperimentalMaterial3Api
	val Context.pendingIntent: PendingIntent
		get() = PendingIntent.getActivity(
				this,
				0,
				MainActivity.newInstance(this),
				PendingIntent.FLAG_IMMUTABLE
			)

	val NOTHING_PLAYING = MediaItem.Builder().setUri("null".toUri()).build()
}