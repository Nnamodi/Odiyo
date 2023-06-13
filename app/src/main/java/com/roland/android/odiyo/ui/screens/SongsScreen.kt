package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
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
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.components.selectSemantics
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.dialog.SortDialog
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
@Composable
fun SongsScreen(
	songs: List<Music>,
	currentSong: Music?,
	playlists: List<Playlist>,
	sortOption: SortOptions,
	playAudio: (Uri, Int?) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	selectedSongs: MutableState<Set<Long>> = rememberSaveable { mutableStateOf(emptySet()) },
	inSelectionMode: (Set<Long>) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val openSortDialog = remember { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val inSelectMode by remember { derivedStateOf { selectedSongs.value.isNotEmpty() } }
	inSelectionMode(selectedSongs.value)

	Scaffold(
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) {
		if (songs.isEmpty()) {
			EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
		} else {
			LazyColumn {
				item {
					if (!inSelectMode) {
						SongListHeader(
							songs = songs,
							showSortAction = true,
							playAllSongs = playAudio,
							openSortDialog = { openSortDialog.value = true }
						)
					}
				}
				itemsIndexed(
					items = songs,
					key = { _, song -> song.id }
				) { index, song ->
					val selected by remember { derivedStateOf { selectedSongs.value.contains(song.id) } }

					MediaItem(
						modifier = Modifier.selectSemantics(
							inSelectionMode = inSelectMode,
							selected = selected,
							onClick = { playAudio(song.uri, index) },
							onLongClick = { if (!inSelectMode) { selectedSongs.value += song.id } },
							toggleSelection = { if (it) selectedSongs.value += song.id else selectedSongs.value -= song.id }
						),
						song = song,
						currentSongUri = currentSong?.uri?.toMediaItem ?: NOTHING_PLAYING,
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
				selectedOption = sortOption,
				onSortPicked = { menuAction(MediaMenuActions.SortSongs(it)) }
			) { openSortDialog.value = it }
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
@Preview
@Composable
fun SongsScreenPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			val currentSong = previewData[2]
			SongsScreen(
				songs = previewData,
				currentSong = currentSong,
				playlists = previewPlaylist,
				sortOption = SortOptions.NameAZ,
				playAudio = { _, _ -> },
				goToCollection = { _, _ -> },
				menuAction = {},
				selectedSongs = remember { mutableStateOf(emptySet()) }
			) {}
		}
	}
}