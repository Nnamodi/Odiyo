package com.roland.android.odiyo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.SelectionModeTopBar
import com.roland.android.odiyo.ui.components.selectSemantics
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddSongsScreen(
	uiState: MediaItemsUiState,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit
) {
	val (_, playlistToAddTo, _, _, _, songs, _, playlists) = uiState
	val selectedSongsId = rememberSaveable { mutableStateOf(emptySet<Long>()) }
	val addingSongs = remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()
	closeSelectionMode(false)

	Scaffold(
		topBar = {
			SelectionModeTopBar(
				numOfSelectedSongs = selectedSongsId.value.size,
				showAddButton = true,
				addSongs = {
					scope.launch {
						addingSongs.value = true
						delay(1000)
						menuAction(
							MediaMenuActions.AddToPlaylist(
								songs = selectedSongs(selectedSongsId.value, songs),
								playlist = getPlaylist(playlistToAddTo, playlists)
							)
						); closeSelectionMode(true)
					}
				}
			) { closeSelectionMode(true) }
		}
	) { paddingValues ->
		if (songs.isEmpty()) {
			if (uiState.isLoading) { LoadingListUi(Modifier.padding(paddingValues)) } else {
				EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
			}
		} else {
			LazyColumn(Modifier.padding(paddingValues), contentPadding = PaddingValues(bottom = 24.dp)) {
				itemsIndexed(
					items = songs,
					key = { _, song -> song.id }
				) { _, song ->
					val selected by remember { derivedStateOf { selectedSongsId.value.contains(song.id) } }

					MediaItem(
						modifier = Modifier.selectSemantics(
							inSelectionMode = true, selected = selected,
							onClick = {}, onLongClick = {},
							toggleSelection = { if (it) selectedSongsId.value += song.id else selectedSongsId.value -= song.id }
						),
						song = song, currentMediaItem = NOTHING_PLAYING,
						inSelectionMode = true, selected = selected
					) {}
				}
			}
		}
	}

	if (addingSongs.value) {
		LoadingUi(loadingText = R.string.setting_up_playlist)
	}

	BackHandler { closeSelectionMode(true) }
}

fun getPlaylist(name: String?, playlists: List<Playlist>): Playlist = playlists.find { it.name == name }!!

@Preview
@Composable
fun AddSongsScreenPreview() {
	OdiyoTheme {
		AddSongsScreen(
			uiState = MediaItemsUiState(songs = previewData.shuffled()),
			menuAction = {}
		) {}
	}
}