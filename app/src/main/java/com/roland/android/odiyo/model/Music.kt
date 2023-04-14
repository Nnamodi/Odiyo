package com.roland.android.odiyo.model

import android.graphics.Bitmap
import android.net.Uri
import kotlin.time.Duration.Companion.milliseconds

data class Music(
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val time: Int,
	val thumbnail: Bitmap?
) {
	val duration = time.milliseconds
}
