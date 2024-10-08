package com.roland.android.data_system.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.flow.MutableStateFlow

object States {

	val isPlaying = MutableStateFlow(false)

	val isDeviceMuted = MutableStateFlow(false)

	val currentDuration = MutableStateFlow(0L)

	val currentMediaItemIndex = MutableStateFlow(0)

	val currentMediaItem = MutableStateFlow<MediaItem?>(null)

	val nowPlayingMetadata = MutableStateFlow<MediaMetadata?>(null)

	// mutable list of MediaItems for populating the Player
	val mediaItemsFlow = MutableStateFlow<MutableList<MediaItem>>(mutableListOf())

}