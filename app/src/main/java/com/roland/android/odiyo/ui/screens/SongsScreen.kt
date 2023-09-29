package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.media3.common.MediaItem
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.states.MediaUiState
import com.roland.android.odiyo.ui.components.*
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.Permissions.rememberPermissionLauncher
import com.roland.android.odiyo.util.Permissions.writeStoragePermission
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.SongDetails

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SongsScreen(
	uiState: MediaUiState,
	playAudio: (Uri, Int?) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val openSortDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }
	val openPermissionDialog = remember { mutableStateOf(false) }
	val writeStoragePermissionGranted = remember { mutableStateOf(false) }
	var permission by remember { mutableStateOf("") }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val selectedSongsId = rememberSaveable { mutableStateOf(emptySet<Long>()) }
	val inSelectMode by remember { derivedStateOf { selectedSongsId.value.isNotEmpty() } }
	val snackbarYOffset = if (inSelectMode) 10.dp else 80.dp
	val requestPermissionLauncher = rememberPermissionLauncher(
		onResult = { writeStoragePermissionGranted.value = it }
	)
	closeSelectionMode(!inSelectMode)

	context.writeStoragePermission({ permission = it }) { isGranted ->
		writeStoragePermissionGranted.value = isGranted
		Log.d("PermissionInfo", "Storage write permission granted: $isGranted")
	}

	Scaffold(
		topBar = {
			if (inSelectMode) {
				SelectionModeTopBar(selectedSongsId.value.size, isSongsScreen = true) { selectedSongsId.value = emptySet() }
			}
		},
		bottomBar = {
			SelectionModeBottomBar(inSelectMode, isSongsScreen = true) {
				val selectedSongs = selectedSongs(selectedSongsId.value, uiState.allSongs)
				when (it) {
					SelectionModeItems.PlayNext -> { menuAction(MediaMenuActions.PlayNext(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToQueue -> { menuAction(MediaMenuActions.AddToQueue(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToPlaylist -> openAddToPlaylistDialog.value = true
					SelectionModeItems.Share -> { menuAction(MediaMenuActions.ShareSong(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.Delete -> {
						openPermissionDialog.value = !writeStoragePermissionGranted.value
						openDeleteDialog.value = writeStoragePermissionGranted.value
					}
				}
				showSnackbar(it, context, scope, snackbarHostState)
			}
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = -snackbarYOffset)) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { paddingValues ->
		if (uiState.allSongs.isEmpty() || uiState.isLoading) {
			if (uiState.isLoading) { LoadingListUi() } else {
				EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
			}
		} else {
			Column(Modifier.padding(top = if (inSelectMode) paddingValues.calculateTopPadding() else 0.dp)) {
				SongListHeader(
					songs = uiState.allSongs, showSortAction = true, inSelectMode = inSelectMode,
					playAllSongs = playAudio, openSortDialog = { openSortDialog.value = true }
				)
				LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
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
							).animateItemPlacement(tween(1000)),
							song = song,
							currentMediaItem = uiState.currentMediaItem ?: MediaItem.EMPTY,
							inSelectionMode = inSelectMode,
							selected = selected,
							openMenuSheet = { songClicked = it; openBottomSheet.value = true }
						)
					}
				}
			}
		}

		if (openBottomSheet.value && songClicked != null) {
			val systemBarHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 16.dp

			MediaItemSheet(
				modifier = Modifier.absoluteOffset(y = -systemBarHeight),
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

		if (openPermissionDialog.value) {
			PermissionDialog(
				permissionMessage = stringResource(R.string.write_storage_permission_message),
				requestPermission = { requestPermissionLauncher.launch(permission) },
				openDialog = { openPermissionDialog.value = it }
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