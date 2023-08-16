package com.roland.android.odiyo.data

data class CurrentPlaylist(
	val playlist: List<String>,
	val currentSongPosition: Int,
	val currentSongSeekPosition: Long
)

data class ShuffleState(
	val state: Boolean,
	val randomSeed: Int
)

const val LIST_SEPARATOR = "[-]"