package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.states.MediaUiState
import com.roland.android.odiyo.ui.components.*
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.SongDetails

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
@Composable
fun SongsScreen(
	uiState: MediaUiState,
	playAudio: (Uri, Int?) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	inSelectionMode: (Boolean) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val openSortDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val selectedSongsId = rememberSaveable { mutableStateOf(emptySet<Long>()) }
	val inSelectMode by remember { derivedStateOf { selectedSongsId.value.isNotEmpty() } }
	inSelectionMode(!inSelectMode)

	Scaffold(
		topBar = {
			if (inSelectMode) {
				SelectionModeTopBar(selectedSongsId.value.size) { selectedSongsId.value = emptySet() }
			}
		},
		bottomBar = {
			SelectionModeBottomBar(inSelectMode) {
				val selectedSongs = selectedSongs(selectedSongsId.value, uiState.allSongs)
				when (it) {
					SelectionModeItems.PlayNext -> { menuAction(MediaMenuActions.PlayNext(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToQueue -> { menuAction(MediaMenuActions.AddToQueue(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToPlaylist -> openAddToPlaylistDialog.value = true
					SelectionModeItems.Share -> { menuAction(MediaMenuActions.ShareSong(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.Delete -> openDeleteDialog.value = true
				}
				showSnackbar(it, context, scope, snackbarHostState)
			}
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { paddingValues ->
		if (uiState.allSongs.isEmpty()) {
			EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
		} else {
			LazyColumn(Modifier.padding(paddingValues)) {
				item {
					SongListHeader(
						songs = uiState.allSongs, showSortAction = true, inSelectMode = inSelectMode,
						playAllSongs = playAudio, openSortDialog = { openSortDialog.value = true }
					)
				}
				itemsIndexed(
					items = uiState.allSongs,
					key = { _, song -> song.id }
				) { index, song ->
					val selected by remember { derivedStateOf { selectedSongsId.value.contains(song.id) } }

					MediaItem(
						modifier = Modifier.selectSemantics(
							inSelectionMode = inSelectMode,
							selected = selected,
							onClick = { playAudio(song.uri, index) },
							onLongClick = { if (!inSelectMode) { selectedSongsId.value += song.id } },
							toggleSelection = { if (it) selectedSongsId.value += song.id else selectedSongsId.value -= song.id }
						),
						song = song,
						currentMediaItem = uiState.currentMediaItem,
						inSelectionMode = inSelectMode,
						selected = selected,
						openMenuSheet = { songClicked = it; openBottomSheet.value = true }
					)
				}
			}
		}

		if (openBottomSheet.value && songClicked != null) {
			MediaItemSheet(
				song = songClicked!!,
				scaffoldState = sheetState,
				goToCollection = goToCollection,
				openBottomSheet = { openBottomSheet.value = it },
				openAddToPlaylistDialog = { openAddToPlaylistDialog.value = true; openBottomSheet.value = false },
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				}
			)
		}

		if (openSortDialog.value) {
			SortDialog(
				selectedOption = uiState.sortOption,
				onSortPicked = { menuAction(MediaMenuActions.SortSongs(it)) }
			) { openSortDialog.value = it }
		}

		if (openAddToPlaylistDialog.value &&
			(songClicked != null || selectedSongsId.value.isNotEmpty())) {
			val selectedSongs = if (inSelectMode) {
				selectedSongs(selectedSongsId.value, uiState.allSongs)
			} else listOf(songClicked!!)

			AddToPlaylistDialog(
				songs = selectedSongs,
				playlists = uiState.playlists,
				addSongToPlaylist = {
					selectedSongsId.value = emptySet()
					menuAction(it); openDeleteDialog.value = false
					showSnackbar(it, context, scope, snackbarHostState)
				},
				openDialog = { openAddToPlaylistDialog.value = it }
			)
		}

		if (openDeleteDialog.value) {
			DeleteDialog(
				delete = {
					val selectedSongs = selectedSongs(selectedSongsId.value, uiState.allSongs)
					menuAction(
						MediaMenuActions.DeleteSongs(
							selectedSongs.map { SongDetails(it.id, it.uri) }
						)
					)
					openDeleteDialog.value = false
				},
				openDialog = { openDeleteDialog.value = it },
				multipleSongs = selectedSongsId.value.size > 1
			)
		}
	}

	if (inSelectMode) {
		BackHandler { selectedSongsId.value = emptySet() }
	}
}

fun selectedSongs(songsId: Set<Long>, songs: List<Music>): List<Music> {
	val matchingSongs = mutableListOf<Music>()
	songsId.forEach { id ->
		if (songs.map { it.id }.contains(id)) {
			songs.find { it.id == id }?.let(matchingSongs::add)
		}
	}
	return matchingSongs
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun SongsScreenPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			SongsScreen(
				uiState = MediaUiState(allSongs = previewData),
				playAudio = { _, _ -> },
				goToCollection = { _, _ -> },
				menuAction = {}
			) {}
		}
	}
}