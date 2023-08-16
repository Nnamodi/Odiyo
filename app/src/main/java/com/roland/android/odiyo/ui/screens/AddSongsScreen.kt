package com.roland.android.odiyo.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.roland.android.odiyo.ui.components.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AddSongsScreen(
	uiState: MediaItemsUiState,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit
) {
	val (_, playlistToAddTo, _, _, _, songs, _, playlists) = uiState
	val selectedSongsId = rememberSaveable { mutableStateOf(emptySet<Long>()) }
	closeSelectionMode(false)

	Scaffold(
		topBar = {
			SelectionModeTopBar(
				numOfSelectedSongs = selectedSongsId.value.size,
				showAddButton = true,
				addSongs = {
					menuAction(
						MediaMenuActions.AddToPlaylist(
							songs = selectedSongs(selectedSongsId.value, songs),
							playlist = getPlaylist(playlistToAddTo, playlists)
						)
					); closeSelectionMode(true)
				}
			) { closeSelectionMode(true) }
		}
	) { paddingValues ->
		if (songs.isEmpty()) {
			EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
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

	BackHandler { closeSelectionMode(true) }
}

fun getPlaylist(name: String?, playlists: List<Playlist>): Playlist = playlists.find { it.name == name }!!

@RequiresApi(Build.VERSION_CODES.Q)
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