package com.roland.android.player.util

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem

object Constants {
	const val MUSIC_NOTIFICATION_ID = 9570
	const val MUSIC_NOTIFICATION_CHANNEL_ID = "roland.odiyo_music_notification"

	val Uri.toMediaItem: MediaItem
		get() = MediaItem.Builder().setUri(this).build()

	val NOTHING_PLAYING = MediaItem.Builder()
		.setUri("null".toUri())
		.build()

	// Error codes
	const val UNRECOGNIZED_INPUT_FORMAT_EXCEPTION = 3003
}