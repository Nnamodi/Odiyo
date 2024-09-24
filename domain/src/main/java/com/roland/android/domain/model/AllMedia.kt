package com.roland.android.domain.model

data class AllMedia(
	val allSongs: List<Music>,
	val albums: List<Album>,
	val artists: List<Artist>
)
