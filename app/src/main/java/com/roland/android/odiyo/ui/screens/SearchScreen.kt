package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.SearchBar
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.menu.SongListMenu
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun SearchScreen(
	searchQuery: String,
	searchResult: List<Music>,
	playlists: List<Playlist>,
	onTextChange: (String) -> Unit,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	goToCollection: (String, String) -> Unit,
	clearSearchQuery: () -> Unit,
	closeSearchScreen: () -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val openMenu = remember { mutableStateOf(false) }
	val openBottomSheet = remember { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val yOffset by remember { mutableStateOf(160) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = {
			SearchBar(
				query = searchQuery,
				onTextChange = onTextChange,
				clearSearchQuery = clearSearchQuery,
				closeSearchScreen = closeSearchScreen
			)
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { paddingValues ->
		if (searchQuery.isEmpty()) {
			EmptyListScreen(
				text = stringResource(R.string.type_to_search),
				modifier = Modifier.padding(paddingValues)
			)
		} else {
			LazyColumn(Modifier.padding(paddingValues)) {
				item {
					SongListHeader(
						songs = searchResult,
						songsFromSearch = true,
						playAllSongs = { _, _ -> },
						openMenu = { openMenu.value = true }
					)
				}
				itemsIndexed(
					items = searchResult,
					key = { _, song -> song.id }
				) { index, song ->
					MediaItem(
						song = song,
						currentSongUri = currentSong?.uri?.toMediaItem ?: NOTHING_PLAYING,
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

		if (openMenu.value) {
			SongListMenu(
				songs = searchResult,
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				},
				yOffset = yOffset,
			) { openMenu.value = it }
		}

		if (openAddToPlaylistDialog.value) {
			AddToPlaylistDialog(
				song = songClicked!!,
				playlists = playlists,
				addSongToPlaylist = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				},
				openDialog = { openAddToPlaylistDialog.value = it }
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun SearchScreenPreview() {
	OdiyoTheme {
		SearchScreen(
			searchQuery = "a",
			searchResult = previewData.shuffled(),
			playlists = previewPlaylist,
			onTextChange = {},
			currentSong = previewData[3],
			playAudio = { _, _ -> },
			menuAction = {},
			goToCollection = { _, _ -> },
			clearSearchQuery = {}
		) {}
	}
}