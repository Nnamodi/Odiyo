package com.roland.android.domain.model

import android.graphics.Bitmap
import android.net.Uri
import java.util.Date

data class Music(
	val id: Long,
	val uri: Uri,
	val name: String,
	var title: String,
	var artist: String,
	val time: Long,
	val bytes: Int = 0,
	val addedOn: Long = 0,
	val album: String = "",
	val path: String = "",

	val duration: String = "",
	val size: String = "",
	val dateAdded: String = "",
	val artwork: Bitmap,

	val generatedId: Int = 0,
	val favorite: Boolean = false,
	val lastPlayed: Date = Date(0),
	val timesPlayed: Int? = 0
)