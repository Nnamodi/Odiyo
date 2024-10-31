package com.roland.android.odiyo.ui.screens.search

import androidx.media3.common.MediaItem
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.util.SortOptions
import com.roland.android.odiyo.data.State

data class SearchUiState(
	val searchQuery: String = "",
	val searchResult: State<List<Music>> = State.Loading,
	val searchHistory: List<String> = emptyList(),
	val allSongs: List<Music> = emptyList(),
	val sortOption: SortOptions = SortOptions.NameAZ,
	val playlists: List<Playlist> = emptyList(),
	val currentMediaItem: MediaItem = MediaItem.EMPTY
)