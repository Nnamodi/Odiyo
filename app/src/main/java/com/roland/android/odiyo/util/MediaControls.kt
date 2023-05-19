package com.roland.android.odiyo.util

sealed interface MediaControls {
	object PlayPause : MediaControls
	object Shuffle : MediaControls
	object Mute : MediaControls
	data class SeekToPosition(val position: Long) : MediaControls
	data class Seek(val previous: Boolean, val next: Boolean) : MediaControls
}