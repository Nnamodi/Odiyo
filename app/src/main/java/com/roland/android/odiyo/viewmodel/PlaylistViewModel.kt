package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.util.PlaylistMenuActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class PlaylistViewModel(
	appDataStore: AppDataStore,
	musicRepository: MusicRepository,
	mediaRepository: MediaRepository,
	private val playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {

	fun playlistActions(action: PlaylistMenuActions) {
		when (action) {
			is PlaylistMenuActions.AddToQueue -> {}
			is PlaylistMenuActions.CreatePlaylist -> createPlaylist(action.playlist)
			is PlaylistMenuActions.DeletePlaylist -> deletePlaylist(action.playlist)
			is PlaylistMenuActions.PlayNext -> {}
			is PlaylistMenuActions.RenamePlaylist -> updatePlaylist(action.playlist)
		}
	}

	private fun createPlaylist(playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistRepository.createPlaylist(playlist)
		}
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