package com.roland.android.domain.model

import android.net.Uri

data class Artist(
	val uri: Uri,
	val numberOfTracks: Int,
	val artist: String
)
