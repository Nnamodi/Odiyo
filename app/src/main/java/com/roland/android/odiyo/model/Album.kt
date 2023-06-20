package com.roland.android.odiyo.model

import android.net.Uri

data class Album(
	val uri: Uri,
	val numberOfSongs: Int,
	val album: String
)
