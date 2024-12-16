package com.roland.android.odiyo.ui.screens.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
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
import androidx.media3.common.MediaItem
import com.roland.android.domain.model.Playlist
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.appbars.SelectionModeTopBar
import com.roland.android.odiyo.ui.components.selectSemantics
import com.roland.android.odiyo.ui.screens.CommonScreen
import com.roland.android.odiyo.ui.screens.LoadingListUi
import com.roland.android.odiyo.ui.screens.LoadingUi
import com.roland.android.odiyo.ui.screens.media.tabs.selectedSongs
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.actions.MediaMenuActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddSongsScreen(
	uiState: ListUiState,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit
) {
	val (songsState, collectionName, _, _, playlists) = uiState
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
					val songs = if (songsState is State.Success) songsState.data else emptyList()
					scope.launch {
						addingSongs.value = true
						delay(1000)
						menuAction(
							MediaMenuActions.AddToPlaylist(
								songs = selectedSongs(selectedSongsId.value, songs),
								playlist = getPlaylist(collectionName, playlists)
							)
						); closeSelectionMode(true)
					}
				}
			) { closeSelectionMode(true) }
		}
	) { paddingValues ->
		CommonScreen(
			state = songsState,
			loadingScreen = { LoadingListUi(Modifier.padding(paddingValues)) }
		) { songs ->
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
						).animateItem(
							fadeInSpec = null,
							fadeOutSpec = null,
							placementSpec = tween(1000)
						),
						song = song,
						currentMediaItem = MediaItem.EMPTY,
						inSelectionMode = true, selected = selected
					) {}
				}
			}

			if (songs.isEmpty()) {
				EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
			}
		}
	}

	if (addingSongs.value) LoadingUi()

	BackHandler { closeSelectionMode(true) }
}

fun getPlaylist(
	name: String?,
	playlists: List<Playlist>
): Playlist = playlists.find { it.name == name }!!

@Preview
@Composable
fun AddSongsScreenPreview() {
	OdiyoTheme {
		AddSongsScreen(
			uiState = ListUiState(songs = State.Success(previewData.shuffled())),
			menuAction = {}
		) {}
	}
}