package com.roland.android.data_local.datastore

data class CurrentPlaylist(
	val playlist: List<String>,
	val currentSongPosition: Int,
	val currentSongSeekPosition: Long
)