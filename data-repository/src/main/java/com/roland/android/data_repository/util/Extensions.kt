package com.roland.android.data_repository.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.roland.android.data_repository.R
import com.roland.android.data_repository.util.Constants.DATE
import com.roland.android.data_repository.util.Constants.MB_DIVISOR
import com.roland.android.data_repository.util.Constants.MB_FORMAT
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Extensions {

	val Long.date: String
		get() = SimpleDateFormat(DATE, Locale.getDefault()).format(this * 1000)

	val Int.toMb: String
		get() = DecimalFormat(MB_FORMAT).format(this / MB_DIVISOR)

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
		return if (numOfSongs > 0) {
			songs[numOfSongs - 1].getMediaArt(context) ?: defaultArt
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

}