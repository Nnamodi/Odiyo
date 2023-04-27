package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
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

	val playingState = MutableStateFlow(false)

	val Long.time: String
		get() = LocalDateTime.ofInstant(
			Instant.ofEpochMilli(this),
			ZoneId.systemDefault()
		).format(
			DateTimeFormatter.ofPattern(
				"mm:ss",
				Locale.getDefault()
			)
		)

	val Uri.toMediaItem: MediaItem
		get() = MediaItem.Builder().setUri(this).build()

	// an intent to launch UI from player notification [dysfunctional]
	val Context.pendingIntent: PendingIntent
		get() = PendingIntent.getActivity(
				this,
				0,
				MainActivity.newInstance(this),
				PendingIntent.FLAG_IMMUTABLE
			)

	val NOTHING_PLAYING = MediaItem.Builder().setUri("null".toUri()).build()
}