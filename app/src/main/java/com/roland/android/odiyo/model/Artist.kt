package com.roland.android.odiyo.model

import android.net.Uri

data class Artist(
	val id: Uri,
	private val numTracks: String,
	val artist: String
) {
	val numberOfTracks = if (numTracks == "1") {
		"$numTracks song"
	} else {
		"$numTracks songs"
	}
}
