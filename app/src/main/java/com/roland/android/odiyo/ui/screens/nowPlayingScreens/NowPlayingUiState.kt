package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.Playlist

data class NowPlayingUiState(
	val currentDuration: String = "00:00",
	val seekProgress: Float = 0f,
	val isPlaying: Boolean = false,
	val deviceMuted: Boolean = false,
	val shuffleState: Boolean = false,
	val repeatMode: Int = 0,
	val currentSongIndex: Int = 0,
	val musicQueue: List<Music> = emptyList(),
	val playlists: List<Playlist> = emptyList(),
	val nowPlayingFrom: NowPlayingFrom = NowPlayingFrom()
)

val Long.time: String
	get() {
		val hour = ((this / 1000) / 60) / 60
		val minute = ((this / 1000) / 60) % 60
		val second = (this / 1000) % 60
		val hours = if (hour > 0) "$hour:" else ""
		val minutes = if (minute < 10) "0$minute:" else "$minute:"
		val seconds = if (second < 10) "0$second" else "$second"
		return "$hours$minutes$seconds"
	}