package com.roland.android.odiyo.ui.screens.home

import androidx.media3.common.MediaItem
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.odiyo.data.State

data class HomeUiState(
	val recentlyAddedSongs: State<List<Music>> = State.Loading,
	val playlists: List<Playlist> = emptyList(),
	val currentMediaItem: MediaItem = MediaItem.EMPTY
)