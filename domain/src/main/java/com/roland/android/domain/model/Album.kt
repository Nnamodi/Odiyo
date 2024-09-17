package com.roland.android.domain.model

import android.net.Uri

data class Album(
	val uri: Uri,
	val numberOfSongs: Int,
	val album: String
)
