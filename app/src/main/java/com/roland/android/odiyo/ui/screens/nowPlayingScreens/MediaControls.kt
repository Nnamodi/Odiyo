package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import com.roland.android.domain.model.Music

sealed interface MediaControls {

	data object PlayPause : MediaControls

	data object Shuffle : MediaControls

	data object Mute : MediaControls

	data object RepeatMode: MediaControls

	data class Favorite(val song: Music): MediaControls

	data class Share(val song: Music) : MediaControls

	data class SeekToPosition(val position: Long) : MediaControls

	data class Seek(val previous: Boolean, val next: Boolean) : MediaControls

}