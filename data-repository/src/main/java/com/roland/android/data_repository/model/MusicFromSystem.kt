package com.roland.android.data_repository.model

import android.net.Uri

data class MusicFromSystem(
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val millis: Long,
	val bytes: Int = 0,
	val addedOn: Long = 0,
	val album: String = "",
	val path: String = ""
)