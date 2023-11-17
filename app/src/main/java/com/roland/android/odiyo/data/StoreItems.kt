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

data class NowPlayingFrom(
	val collectionType: String = "",
	val collectionName: String = ""
)

const val LIST_SEPARATOR = "[-]"