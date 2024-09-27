package com.roland.android.domain.model

import android.graphics.Bitmap
import android.net.Uri
import java.util.Date

data class Music(
	val generatedId: Int = 0,
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val album: String,
	val path: String,
	val addedOn: String,
	val duration: String,
	val size: String,
	val artwork: Bitmap? = null,
	val favorite: Boolean = false,
	val lastPlayed: Date = Date(0),
	val timesPlayed: Int = 0
)