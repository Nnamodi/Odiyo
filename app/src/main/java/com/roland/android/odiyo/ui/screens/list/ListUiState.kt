package com.roland.android.odiyo.ui.screens.list

import androidx.media3.common.MediaItem
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.util.SortOptions
import com.roland.android.odiyo.data.State

data class ListUiState(
	val songs: State<List<Music>> = State.Loading,
	val collectionName: String = "",
	val collectionType: String = "",
	val sortOption: SortOptions = SortOptions.NameAZ,
	val playlists: List<Playlist> = emptyList(),
	val currentMediaItem: MediaItem = MediaItem.EMPTY
)