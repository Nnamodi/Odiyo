package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
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
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.MediaItemsAppBar
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.menu.SongListMenu
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MediaItemsScreen(
	songs: List<Music>,
	collectionName: String,
	collectionType: String,
	currentSong: Music?,
	playlists: List<Playlist>,
	sortOption: SortOptions,
	playAudio: (Uri, Int?) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	navigateUp: () -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = remember { mutableStateOf(false) }
	val openMenu = rememberSaveable { mutableStateOf(false) }
	val openAddToPlaylistDialog = rememberSaveable { mutableStateOf(false) }
	val openSortDialog = rememberSaveable { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = {
			MediaItemsAppBar(
				collectionName = collectionName,
				navigateUp = navigateUp,
				songsNotEmpty = songs.isNotEmpty()
			) { openMenu.value = true }
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { innerPadding ->
		if (songs.isEmpty()) {
			EmptyListScreen(
				text = stringResource(R.string.nothing_here),
				modifier = Modifier.padding(innerPadding),
				playlistCollection = collectionType == PLAYLISTS
			)
		} else {
			LazyColumn(Modifier.padding(innerPadding)) {
				item {
					SongListHeader(
						songs = songs,
						playAllSongs = playAudio
					)
				}
				itemsIndexed(
					items = songs,
					key = { _, song -> song.id }
				) { index, song ->
					MediaItem(
						song = song,
						currentSongUri = currentSong?.uri?.toMediaItem ?: NOTHING_PLAYING,
						openMenuSheet = { songClicked = it; openBottomSheet.value = true }
					)
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
						menuAction(MediaMenuActions.RemoveFromPlaylist(it, collectionName))
						showSnackbar(
							MediaMenuActions.RemoveFromPlaylist(it, collectionName),
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
						showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
					},
					showSortAction = collectionType != LAST_PLAYED,
					openSortDialog = { openSortDialog.value = it }
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

			if (openSortDialog.value) {
				SortDialog(
					selectedOption = sortOption,
					onSortPicked = { menuAction(MediaMenuActions.SortSongs(it)) }
				) { openSortDialog.value = it }
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaItemsScreenPreview() {
	OdiyoTheme {
		MediaItemsScreen(
			songs = previewData.takeLast(5),
			collectionName = "Does it have to be me?",
			collectionType = ALBUMS,
			currentSong = previewData[5],
			playlists = previewPlaylist,
			sortOption = SortOptions.NameAZ,
			playAudio = { _, _ -> },
			goToCollection = { _, _ -> },
			menuAction = {}
		) {}
	}
}