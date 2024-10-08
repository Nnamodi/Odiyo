package com.roland.android.data_system.model

import android.net.Uri

data class SongDetails(
	val id: Long,
	val uri: Uri,
	val title: String? = null,
	val artist: String? = null
)
