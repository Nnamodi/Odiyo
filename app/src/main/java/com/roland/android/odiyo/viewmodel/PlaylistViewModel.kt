package com.roland.android.odiyo.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.util.PlaylistMenuActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
	appDataStore: AppDataStore,
	musicRepository: MusicRepository,
	mediaRepository: MediaRepository,
	private val playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {

	fun playlistActions(action: PlaylistMenuActions) {
		if (!canAccessStorage) return
		when (action) {
			is PlaylistMenuActions.AddToQueue -> addToQueue(action.playlist.name)
			is PlaylistMenuActions.CreatePlaylist -> createPlaylist(action.playlist)
			is PlaylistMenuActions.DeletePlaylist -> deletePlaylist(action.playlist)
			is PlaylistMenuActions.PlayNext -> playNext(action.playlist.name)
			is PlaylistMenuActions.RenamePlaylist -> updatePlaylist(action.playlist)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "playlistAction: $action")
	}

	private fun playNext(playlistName: String) {
		fetchPlaylistSongs(playlistName)
		playNext(mediaItemsScreenUiState.songs, PLAYLISTS, playlistName)
	}

	private fun addToQueue(playlistName: String) {
		fetchPlaylistSongs(playlistName)
		addToQueue(mediaItemsScreenUiState.songs, PLAYLISTS, playlistName)
	}

	private fun updatePlaylist(playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistRepository.updatePlaylist(playlist)
		}
	}

	private fun deletePlaylist(playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistRepository.deletePlaylist(playlist)
		}
	}
}