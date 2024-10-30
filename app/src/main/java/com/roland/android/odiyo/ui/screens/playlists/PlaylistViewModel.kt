package com.roland.android.odiyo.ui.screens.playlists

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.MusicQueueRepository
import com.roland.android.domain.repository.PlaylistUtilRepository
import com.roland.android.domain.usecase.CollectionType
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetSongsFromCollectionUseCase
import com.roland.android.odiyo.service.Util.readStoragePermissionGranted
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.player.player_utils.States.mediaItemsFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistViewModel : ViewModel(), KoinComponent {
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()
	private val getSongsFromCollectionUseCase by inject<GetSongsFromCollectionUseCase>()
	private val musicQueueRepository by inject<MusicQueueRepository>()
	private val playlistUtilRepository by inject<PlaylistUtilRepository>()

	var playlists by mutableStateOf<List<Playlist>>(emptyList()); private set
	private var canAccessStorage by mutableStateOf(false)

	init {
		viewModelScope.launch {
			getPlaylistsUseCase().collect { playlistList ->
				playlists = playlistList
			}
		}
		viewModelScope.launch {
			readStoragePermissionGranted.collectLatest { isGranted ->
				canAccessStorage = isGranted
			}
		}
	}

	fun playlistActions(action: PlaylistMenuActions) {
		if (!canAccessStorage) return
		when (action) {
			is PlaylistMenuActions.AddToQueue -> addToQueue(action.playlist.name)
			is PlaylistMenuActions.CreatePlaylist -> createPlaylist(action.playlist)
			is PlaylistMenuActions.DeletePlaylist -> deletePlaylist(action.playlist)
			is PlaylistMenuActions.PlayNext -> playNext(action.playlist.name)
			is PlaylistMenuActions.RenamePlaylist -> updatePlaylist(action.playlist)
		}
//		updateMusicQueue()
		Log.d("ViewModelInfo", "playlistAction: $action")
	}

	private fun playNext(playlistName: String) {
		viewModelScope.launch {
			getSongsFromCollectionUseCase(
				collectionType = CollectionType.FromPlaylist(playlistName)
			).collect { songs ->
				val nowPlayingFrom = NowPlayingFrom(playlistName, PLAYLISTS).takeIf {
					songs.size > mediaItemsFlow.value.size
				}
				musicQueueRepository.playNext(songs, nowPlayingFrom)
			}
		}
	}

	private fun addToQueue(playlistName: String) {
		viewModelScope.launch {
			getSongsFromCollectionUseCase(
				collectionType = CollectionType.FromPlaylist(playlistName)
			).collect { songs ->
				val nowPlayingFrom = NowPlayingFrom(playlistName, PLAYLISTS).takeIf {
					songs.size > mediaItemsFlow.value.size
				}
				musicQueueRepository.playNext(songs, nowPlayingFrom)
			}
		}
	}

	private fun createPlaylist(playlist: Playlist) {
		playlistUtilRepository.createPlaylist(playlist)
	}

	private fun updatePlaylist(playlist: Playlist) {
		playlistUtilRepository.updatePlaylist(playlist)
	}

	private fun deletePlaylist(playlist: Playlist) {
		playlistUtilRepository.deletePlaylist(playlist)
	}
}