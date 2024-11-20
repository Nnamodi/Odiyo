package com.roland.android.odiyo.ui.screens.list

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.usecase.CollectionType
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetSongsFromCollectionUseCase
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.navigation.FAVORITES
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.util.actions.MediaMenuActions
import com.roland.android.odiyo.util.actions.MediaMenuActionsImpl
import com.roland.android.player.player_utils.States.currentMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListViewModel : ViewModel(), KoinComponent {
	private val getSongsFromCollection by inject<GetSongsFromCollectionUseCase>()
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()
	private val musicUtilRepository by inject<MusicUtilRepository>()
	private val mediaMenuActionsImpl by inject<MediaMenuActionsImpl>()

	private val _listUiState = MutableStateFlow(ListUiState())
	var listUiState by mutableStateOf(_listUiState.value); private set
	private var canAccessStorage by mutableStateOf(false)

	init {
		viewModelScope.launch {
			combine(
				musicUtilRepository.getSortOption(),
				getPlaylistsUseCase(),
				currentMediaItem,
				musicUtilRepository.getPermissionStatus()
			) { sortOption, playlists, mediaItem, permissionStatus ->
				_listUiState.update {
					it.copy(
						sortOption = sortOption,
						playlists = playlists,
						currentMediaItem = mediaItem ?: MediaItem.EMPTY
					)
				}
				canAccessStorage = permissionStatus
			}
		}
		viewModelScope.launch {
			_listUiState.collect {
				listUiState = it
			}
		}
	}

	fun getSongsFromCollection(collectionName: String, collectionType: String) {
		viewModelScope.launch {
			val type = getCollectionType(collectionName, collectionType)
			getSongsFromCollection(type).collect { songs ->
				_listUiState.update { it.copy(songs = State.Success(songs)) }
			}
		}
	}

	fun menuAction(action: MediaMenuActions) {
		if (!canAccessStorage) return
		mediaMenuActionsImpl.actions(action)
//		updateMusicQueue()
		Log.d("ViewModelInfo", "menuAction: $action")
	}

	private fun getCollectionType(
		collectionName: String,
		collectionType: String
	) = when (collectionType) {
		LAST_PLAYED -> CollectionType.LastPlayed
		FAVORITES -> CollectionType.Favorites
		ALBUMS -> CollectionType.FromAlbum(collectionName)
		ARTISTS -> CollectionType.FromAlbum(collectionName)
		else -> CollectionType.FromPlaylist(collectionName) // PLAYLISTS
	}
}