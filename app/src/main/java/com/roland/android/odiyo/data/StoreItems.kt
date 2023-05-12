package com.roland.android.odiyo.data

data class CurrentPlaylist(
	val playlist: List<String>,
	val currentSongPosition: Int,
	val currentSongSeekPosition: Long
)
