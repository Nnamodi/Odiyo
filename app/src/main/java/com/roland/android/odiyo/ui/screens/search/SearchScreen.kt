package com.roland.android.odiyo.ui.screens.search

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
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
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.components.appbars.SearchBar
import com.roland.android.odiyo.ui.components.appbars.SelectionModeBottomBar
import com.roland.android.odiyo.ui.components.appbars.SelectionModeItems
import com.roland.android.odiyo.ui.components.appbars.SelectionModeTopBar
import com.roland.android.odiyo.ui.components.selectSemantics
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.menu.SongListMenu
import com.roland.android.odiyo.ui.navigation.SEARCH
import com.roland.android.odiyo.ui.navigation.Screens
import com.roland.android.odiyo.ui.screens.CommonScreen
import com.roland.android.odiyo.ui.screens.LoadingListUi
import com.roland.android.odiyo.ui.screens.media.tabs.selectedSongs
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.rememberPermissionLauncher
import com.roland.android.odiyo.util.Permissions.writeStoragePermission
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.actions.MediaMenuActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
	uiState: SearchUiState,
	onSearch: (String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit,
	navigate: (Screens) -> Unit
) {
	val (searchQuery, searchResult, _, _, sortOption, playlists, currentMediaItem) = uiState
	val sheetState = rememberModalBottomSheetState(true)
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val openMenu = remember { mutableStateOf(false) }
	val openBottomSheet = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }
	val openSortDialog = remember { mutableStateOf(false) }
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

	LaunchedEffect(true, searchQuery, sortOption) {
		onSearch(searchQuery)
	}

	LaunchedEffect(inSelectMode) {
		closeSelectionMode(!inSelectMode)
	}

	context.writeStoragePermission({ permission = it }) { isGranted ->
		writeStoragePermissionGranted.value = isGranted
		Log.d("PermissionInfo", "Storage write permission granted: $isGranted")
	}

	Scaffold(
		topBar = {
			if (inSelectMode) {
				SelectionModeTopBar(selectedSongsId.value.size) { selectedSongsId.value = emptySet() }
			}
		},
		bottomBar = {
			SelectionModeBottomBar(inSelectMode) {
				val songs = if (searchResult is State.Success) searchResult.data else emptyList()
				val selectedSongs = selectedSongs(selectedSongsId.value, songs)
				when (it) {
					SelectionModeItems.PlayNext -> {
						menuAction(MediaMenuActions.PlayNext(selectedSongs, SEARCH, searchQuery))
						selectedSongsId.value = emptySet()
					}
					SelectionModeItems.AddToQueue -> {
						menuAction(MediaMenuActions.AddToQueue(selectedSongs, SEARCH, searchQuery))
						selectedSongsId.value = emptySet()
					}
					SelectionModeItems.AddToPlaylist -> openAddToPlaylistDialog.value = true
					SelectionModeItems.Share -> {
						menuAction(MediaMenuActions.ShareSong(selectedSongs))
						selectedSongsId.value = emptySet()
					}
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
		CommonScreen(
			state = searchResult,
			loadingScreen = { LoadingListUi(Modifier.padding(paddingValues)) }
		) { songs ->
			Column(Modifier.fillMaxSize()) {
				if (!inSelectMode) SearchBar(
					uiState = uiState,
					onSearch = onSearch,
					navigate = navigate
				) { openMenu.value = true }

				LazyColumn(
					modifier = Modifier.padding(
						top = if (inSelectMode) paddingValues.calculateTopPadding() else 0.dp,
						bottom = paddingValues.calculateBottomPadding()
					),
					contentPadding = PaddingValues(bottom = if (inSelectMode) 24.dp else 100.dp)
				) {
					item {
						SongListHeader(
							songs = songs,
							songsFromSearch = true,
							inSelectMode = inSelectMode,
							playAllSongs = { _, _ -> }
						)
					}
					itemsIndexed(
						items = songs,
						key = { _, song -> song.id }
					) { index, song ->
						val selected by remember { derivedStateOf { selectedSongsId.value.contains(song.id) } }

						MediaItem(
							modifier = Modifier
								.selectSemantics(
									inSelectionMode = inSelectMode,
									selected = selected,
									onClick = {
										val nowPlayingFrom = NowPlayingFrom(searchQuery, SEARCH)
										menuAction(
											MediaMenuActions.PlayAudio(song.uri, index, songs, nowPlayingFrom)
										)
									},
									onLongClick = {
										if (!inSelectMode) {
											selectedSongsId.value += song.id
										}
									},
									toggleSelection = { if (it) selectedSongsId.value += song.id else selectedSongsId.value -= song.id }
								)
								.animateItem(
									fadeInSpec = null,
									fadeOutSpec = null,
									placementSpec = tween(1000)
								),
							song = song,
							currentMediaItem = currentMediaItem,
							inSelectionMode = inSelectMode,
							selected = selected,
							openMenuSheet = { songClicked = it; openBottomSheet.value = true }
						)
					}
				}
				if (searchQuery.isEmpty()) {
					EmptyListScreen(
						text = stringResource(R.string.type_to_search),
						modifier = Modifier.padding(paddingValues)
					)
				}
			}
		}

		if (openBottomSheet.value && songClicked != null) {
			MediaItemSheet(
				song = songClicked!!,
				scaffoldState = sheetState,
				goToCollection = { collectionName, collectionType ->
					navigate(Screens.ListScreen(collectionName, collectionType))
				},
				openBottomSheet = { openBottomSheet.value = it },
				openAddToPlaylistDialog = { openAddToPlaylistDialog.value = true },
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				}
			)
		}

		if (openMenu.value) {
			val songs = if (searchResult is State.Success) searchResult.data else emptyList()

			SongListMenu(
				collectionName = searchQuery,
				collectionType = SEARCH,
				songs = songs,
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState)
				},
				openSortDialog = { openSortDialog.value = it }
			) { openMenu.value = it }
		}

		if (openAddToPlaylistDialog.value &&
			(songClicked != null || selectedSongsId.value.isNotEmpty())) {
			val selectedSongs = if (inSelectMode && searchResult is State.Success) {
				selectedSongs(selectedSongsId.value, searchResult.data)
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

		if (openDeleteDialog.value) {
			DeleteDialog(
				delete = {
					val songs = if (searchResult is State.Success) searchResult.data else emptyList()
					val selectedSongs = selectedSongs(selectedSongsId.value, songs)
					menuAction(MediaMenuActions.DeleteSongs(selectedSongs))
					openDeleteDialog.value = false
				},
				openDialog = { openDeleteDialog.value = it },
				multipleSongs = selectedSongsId.value.size > 1
			)
		}

		if (openSortDialog.value) {
			SortDialog(
				selectedOption = sortOption,
				onSortPicked = { menuAction(MediaMenuActions.SortSongs(it)) }
			) { openSortDialog.value = false }
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
fun SearchScreenPreview() {
	OdiyoTheme {
		SearchScreen(
			uiState = SearchUiState(
				searchQuery = "a",
				searchResult = State.Success(previewData.take(7))
			),
			onSearch = {},
			menuAction = {},
			closeSelectionMode = {},
		) {}
	}
}