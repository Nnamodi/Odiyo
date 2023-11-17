package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.MediaItemsAppBar
import com.roland.android.odiyo.ui.components.SelectionModeBottomBar
import com.roland.android.odiyo.ui.components.SelectionModeItems
import com.roland.android.odiyo.ui.components.SelectionModeTopBar
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.components.selectSemantics
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.menu.SongListMenu
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.Permissions
import com.roland.android.odiyo.util.Permissions.writeStoragePermission
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.SongDetails

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaItemsScreen(
	uiState: MediaItemsUiState,
	previousScreenIsNowPlayingScreen: Boolean,
	playAudio: (Uri, Int, String, String) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit,
	moveToAddSongsScreen: (String) -> Unit,
	navigateUp: () -> Unit
) {
	val (currentMediaItem, collectionName, collectionType, _, _, songs, _, playlists, sortOption) = uiState
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = remember { mutableStateOf(false) }
	val openMenu = rememberSaveable { mutableStateOf(false) }
	val openAddToPlaylistDialog = rememberSaveable { mutableStateOf(false) }
	val openSortDialog = rememberSaveable { mutableStateOf(false) }
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
	val lazyColumnBottomPadding = if (inSelectMode) 24.dp else 100.dp
	val requestPermissionLauncher = Permissions.rememberPermissionLauncher(
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
				SelectionModeTopBar(selectedSongsId.value.size) { selectedSongsId.value = emptySet() }
			} else {
				MediaItemsAppBar(
					collectionName = collectionName, collectionIsPlaylist = collectionType == PLAYLISTS,
					songsNotEmpty = songs.isNotEmpty(), addSongs = moveToAddSongsScreen, navigateUp = navigateUp
				) { openMenu.value = true }
			}
		},
		bottomBar = {
			SelectionModeBottomBar(inSelectMode, collectionIsPlaylist = collectionType == PLAYLISTS) {
				val selectedSongs = selectedSongs(selectedSongsId.value, songs)
				when (it) {
					SelectionModeItems.PlayNext -> { menuAction(MediaMenuActions.PlayNext(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToQueue -> { menuAction(MediaMenuActions.AddToQueue(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.AddToPlaylist -> openAddToPlaylistDialog.value = true
					SelectionModeItems.Share -> { menuAction(MediaMenuActions.ShareSong(selectedSongs)); selectedSongsId.value = emptySet() }
					SelectionModeItems.Delete -> if (collectionType == PLAYLISTS) {
						menuAction(MediaMenuActions.RemoveFromPlaylist(selectedSongs, collectionName)); selectedSongsId.value = emptySet()
					} else {
						openPermissionDialog.value = !writeStoragePermissionGranted.value
						openDeleteDialog.value = writeStoragePermissionGranted.value
					}
				}
				showSnackbar(it, context, scope, snackbarHostState, collectionType == PLAYLISTS)
			}
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = -snackbarYOffset)) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { innerPadding ->
		if (songs.isEmpty() || uiState.isLoading) {
			if (uiState.isLoading) { LoadingListUi(Modifier.padding(innerPadding)) } else {
				EmptyListScreen(
					text = stringResource(R.string.nothing_here),
					modifier = Modifier.padding(innerPadding),
					playlistCollection = collectionType == PLAYLISTS,
					addSongs = { moveToAddSongsScreen(collectionName) }
				)
			}
		} else {
			LazyColumn(Modifier.padding(innerPadding), contentPadding = PaddingValues(bottom = lazyColumnBottomPadding)) {
				item {
					SongListHeader(
						songs = songs,
						inSelectMode = inSelectMode,
						playAllSongs = { uri, index ->
							playAudio(uri, index, collectionType, collectionName)
						}
					)
				}
				itemsIndexed(
					items = songs,
					key = { _, song -> song.id }
				) { index, song ->
					val selected by remember { derivedStateOf { selectedSongsId.value.contains(song.id) } }

					MediaItem(
						modifier = Modifier.selectSemantics(
							inSelectionMode = inSelectMode,
							selected = selected,
							onClick = { playAudio(song.uri, index, collectionType, collectionName) },
							onLongClick = { if (!inSelectMode) { selectedSongsId.value += song.id } },
							toggleSelection = { if (it) selectedSongsId.value += song.id else selectedSongsId.value -= song.id }
						).animateItemPlacement(tween(1000)),
						song = song,
						currentMediaItem = currentMediaItem ?: MediaItem.EMPTY,
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
				collectionIsPlaylist = collectionType == PLAYLISTS,
				goToCollection = goToCollection,
				openBottomSheet = { openBottomSheet.value = it },
				openAddToPlaylistDialog = { openAddToPlaylistDialog.value = true; openBottomSheet.value = false },
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				},
				removeFromPlaylist = {
					menuAction(MediaMenuActions.RemoveFromPlaylist(listOf(it), collectionName))
					showSnackbar(
						MediaMenuActions.RemoveFromPlaylist(listOf(it), collectionName),
						context, scope, snackbarHostState, it
					)
				}
			)
		}

		if (openMenu.value) {
			SongListMenu(
				songs = songs,
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState)
				},
				showSortAction = collectionType != LAST_PLAYED,
				openSortDialog = { openSortDialog.value = it }
			) { openMenu.value = it }
		}

		if (openAddToPlaylistDialog.value &&
			(songClicked != null || selectedSongsId.value.isNotEmpty())) {
			val selectedSongs = if (inSelectMode) {
				selectedSongs(selectedSongsId.value, songs)
			} else listOf(songClicked!!)

			AddToPlaylistDialog(
				songs = selectedSongs,
				playlists = playlists,
				addSongToPlaylist = {
					selectedSongsId.value = emptySet()
					menuAction(it); openDeleteDialog.value = false
					showSnackbar(it, context, scope, snackbarHostState)
				},
				openDialog = { openAddToPlaylistDialog.value = it }
			)
		}

		if (openSortDialog.value) {
			SortDialog(
				selectedOption = sortOption,
				onSortPicked = { menuAction(MediaMenuActions.SortSongs(it)) }
			) { openSortDialog.value = it }
		}

		if (openDeleteDialog.value) {
			DeleteDialog(
				delete = {
					val selectedSongs = selectedSongs(selectedSongsId.value, songs)
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

@Preview
@Composable
fun MediaItemsScreenPreview() {
	OdiyoTheme {
		val uiState by remember { mutableStateOf(
			MediaItemsUiState(
				collectionName = "Does it have to be me?", songs = previewData.takeLast(5),
				playlists = previewPlaylist, sortOption = SortOptions.NameAZ
			)
		) }

		MediaItemsScreen(
			uiState = uiState,
			previousScreenIsNowPlayingScreen = false,
			playAudio = { _, _, _, _ -> },
			goToCollection = { _, _ -> },
			menuAction = {},
			closeSelectionMode = {},
			{}
		) {}
	}
}