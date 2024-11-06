package com.roland.android.odiyo.ui.screens.media

import androidx.media3.common.MediaItem
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.util.SortOptions
import com.roland.android.odiyo.data.State

data class MediaUiState(
	val allSongs: State<List<Music>> = State.Loading,
	val allAlbums: State<List<Album>> = State.Loading,
	val allArtists: State<List<Artist>> = State.Loading,
	val sortOption: SortOptions = SortOptions.NameAZ,
	val playlists: List<Playlist> = emptyList(),
	val currentMediaItem: MediaItem = MediaItem.EMPTY
)