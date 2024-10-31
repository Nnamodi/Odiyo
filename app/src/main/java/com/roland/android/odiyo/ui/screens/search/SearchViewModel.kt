package com.roland.android.odiyo.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.roland.android.domain.repository.MusicRepository
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetSearchDataUseCase
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.MediaMenuActionsImpl
import com.roland.android.player.player_utils.States.currentMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel : ViewModel(), KoinComponent {
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()
	private val getSearchDataUseCase by inject<GetSearchDataUseCase>()
	private val mediaMenuActionsImpl by inject<MediaMenuActionsImpl>()
	private val musicRepository by inject<MusicRepository>()
	private val musicUtilRepository by inject<MusicUtilRepository>()

	private val _searchUiState = MutableStateFlow(SearchUiState())
	var searchUiState by mutableStateOf(_searchUiState.value); private set

	init {
		viewModelScope.launch {
			combine(
				musicRepository.getAllSongs(),
				musicUtilRepository.getSortOption(),
				getPlaylistsUseCase(),
				currentMediaItem
			) { allSongs, sortOption, playlists, mediaItem ->
				_searchUiState.update {
					it.copy(
						allSongs = allSongs,
						sortOption = sortOption,
						playlists = playlists,
						currentMediaItem = mediaItem ?: MediaItem.EMPTY
					)
				}
			}
		}
		viewModelScope.launch {
			_searchUiState.collect {
				searchUiState = it
			}
		}
	}

	fun menuAction(actions: MediaMenuActions) {
		mediaMenuActionsImpl.actions(actions)
	}

	fun onSearch(query: String) {
		_searchUiState.update { it.copy(searchQuery = query, searchResult = State.Loading) }
		viewModelScope.launch {
			getSearchDataUseCase(query).collect { searchData ->
				_searchUiState.update {
					it.copy(
						searchResult = State.Success(searchData.searchResult),
						searchHistory = searchData.searchHistory
					)
				}
			}
		}
	}
}