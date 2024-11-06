package com.roland.android.odiyo.ui.screens.media

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.roland.android.domain.repository.MusicQueueRepository
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.repository.PlayerRepository
import com.roland.android.domain.usecase.GetAllMediaUseCase
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.util.AudioIntentActions
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.MediaMenuActionsImpl
import com.roland.android.player.player_utils.States.currentMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaViewModel : ViewModel(), KoinComponent {
	private val getAllMediaUseCase by inject<GetAllMediaUseCase>()
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()
	private val musicUtilRepository by inject<MusicUtilRepository>()
	private val mediaMenuActionsImpl by inject<MediaMenuActionsImpl>()
	private val musicQueueRepository by inject<MusicQueueRepository>()
	private val playerRepository by inject<PlayerRepository>()

	private val _mediaUiState = MutableStateFlow(MediaUiState())
	var mediaUiState by mutableStateOf(_mediaUiState.value); private set
	private var canAccessStorage by mutableStateOf(false)

	init {
		viewModelScope.launch {
			combine(
				getAllMediaUseCase(),
				musicUtilRepository.getSortOption(),
				getPlaylistsUseCase(),
				currentMediaItem,
				musicUtilRepository.getPermissionStatus()
			) { allMedia, sortOption, playlists, mediaItem, permissionStatus ->
				_mediaUiState.update {
					it.copy(
						allSongs = State.Success(allMedia.allSongs),
						allAlbums = State.Success(allMedia.albums),
						allArtists = State.Success(allMedia.artists),
						sortOption = sortOption,
						playlists = playlists,
						currentMediaItem = mediaItem ?: MediaItem.EMPTY
					)
				}
				canAccessStorage = permissionStatus
			}
		}
		viewModelScope.launch {
			_mediaUiState.collect {
				mediaUiState = it
			}
		}
	}

	fun menuAction(action: MediaMenuActions) {
		if (!canAccessStorage) return
		mediaMenuActionsImpl.actions(action)
//		updateMusicQueue()
		Log.d("ViewModelInfo", "menuAction: $action")
	}

	fun audioIntentAction(action: AudioIntentActions) {
		if (!canAccessStorage) return
		when (action) {
			is AudioIntentActions.Play -> playerRepository.playSong(action.uri)
			is AudioIntentActions.PlayNext -> musicQueueRepository.playNext(action.uri)
			is AudioIntentActions.AddToQueue -> musicQueueRepository.addToQueue(action.uri)
		}
//		updateMusicQueue()
		Log.d("ViewModelInfo", "audioIntentAction: $action")
	}
}