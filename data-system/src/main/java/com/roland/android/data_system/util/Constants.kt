package com.roland.android.data_system.util

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem

object Constants {
	val Uri.toMediaItem: MediaItem
		get() = MediaItem.Builder().setUri(this).build()

	val NOTHING_PLAYING = MediaItem.Builder()
		.setUri("null".toUri())
		.build()

	// Error codes
	const val UNRECOGNIZED_INPUT_FORMAT_EXCEPTION = 3003
}