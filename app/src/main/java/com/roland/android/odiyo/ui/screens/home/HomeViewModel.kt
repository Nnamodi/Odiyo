package com.roland.android.odiyo.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetRecentlyAddedSongsUseCase
import com.roland.android.odiyo.data.State
import com.roland.android.player.player_utils.States.currentMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {
	private val getRecentlyAddedSongsUseCase by inject<GetRecentlyAddedSongsUseCase>()
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()
	private val _homeUiState = MutableStateFlow(HomeUiState())
	var homeUiState by mutableStateOf(_homeUiState.value); private set

	init {
		viewModelScope.launch {
			combine(
				getRecentlyAddedSongsUseCase(),
				getPlaylistsUseCase(),
				currentMediaItem
			) { recentlyAddedSongs, playlists, mediaItem ->
				_homeUiState.update {
					it.copy(
						recentlyAddedSongs = State.Success(recentlyAddedSongs),
						playlists = playlists,
						currentMediaItem = mediaItem ?: MediaItem.EMPTY
					)
				}
			}
		}
		viewModelScope.launch {
			_homeUiState.collect {
				homeUiState = it
			}
		}
	}
}