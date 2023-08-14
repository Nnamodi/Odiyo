package com.roland.android.odiyo.states

import android.graphics.Bitmap
import androidx.media3.common.MediaItem
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.ui.dialog.SortOptions

data class NowPlayingUiState(
	val artwork: Bitmap? = null,
	val currentDuration: String = "00:00",
	val seekProgress: Float = 0f,
	val playingState: Boolean = false,
	val deviceMuted: Boolean = false,
	val shuffleState: Boolean = false,
	val repeatMode: Int = 0,
	val currentSongIndex: Int = 0,
	val musicQueue: List<Music> = emptyList(),
	val playlists: List<Playlist> = emptyList()
)

data class MediaUiState(
	val currentMediaItem: MediaItem? = NOTHING_PLAYING,
	val sortOption: SortOptions = SortOptions.NameAZ,
	val allSongs: List<Music> = emptyList(),
	val recentSongs: List<Music> = emptyList(),
	val playlists: List<Playlist> = emptyList(),
	val albums: List<Album> = emptyList(),
	val artists: List<Artist> = emptyList(),
)

data class MediaItemsUiState(
	val currentMediaItem: MediaItem? = NOTHING_PLAYING,
	val collectionName: String = "",
	val collectionType: String = "",
	val searchQuery: String = "",
	val songs: List<Music> = emptyList(),
	val allSongs: List<Music> = emptyList(),
	val playlists: List<Playlist> = emptyList(),
	val sortOption: SortOptions = SortOptions.NameAZ
)