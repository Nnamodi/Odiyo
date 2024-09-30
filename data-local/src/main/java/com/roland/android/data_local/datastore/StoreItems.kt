package com.roland.android.data_local.datastore

data class CurrentPlaylist(
	val playlist: List<String>,
	val currentSongPosition: Int,
	val currentSongSeekPosition: Long
)

data class NowPlayingFrom(
	val collectionType: String = "",
	val collectionName: String = ""
)