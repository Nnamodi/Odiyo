package com.roland.android.domain.model

import android.graphics.Bitmap
import android.net.Uri
import java.util.Date

data class Playlist(
	val id: Int = 0,
	val name: String,
	val songs: List<Uri>,
	val numOfSongs: Int = 0,
	val dateCreated: Date = Date(),
	val dateModified: Date = Date(),
	val artwork: Bitmap? = null
)