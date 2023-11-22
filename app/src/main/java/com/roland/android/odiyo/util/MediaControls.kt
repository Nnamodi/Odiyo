package com.roland.android.odiyo.util

import com.roland.android.odiyo.model.Music

sealed interface MediaControls {

	object PlayPause : MediaControls

	object Shuffle : MediaControls

	object Mute : MediaControls

	object RepeatMode: MediaControls

	data class Favorite(val song: Music): MediaControls

	data class Share(val music: Music) : MediaControls

	data class SeekToPosition(val position: Long) : MediaControls

	data class Seek(val previous: Boolean, val next: Boolean) : MediaControls

}