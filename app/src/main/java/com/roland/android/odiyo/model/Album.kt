package com.roland.android.odiyo.model

import android.net.Uri

data class Album(
	val uri: Uri,
	private val numSongs: String,
	val album: String
) {
	val numberOfSongs = if (numSongs == "1") {
		"$numSongs song"
	} else {
		"$numSongs songs"
	}
}
