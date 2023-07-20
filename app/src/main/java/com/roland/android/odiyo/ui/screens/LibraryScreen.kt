package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.states.MediaUiState
import com.roland.android.odiyo.ui.components.MainAppBar
import com.roland.android.odiyo.ui.components.RecentSongItem
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.navigation.FAVORITES
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.ui.screens.Menus.*
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun LibraryScreen(
	uiState: MediaUiState,
	playSong: (Uri, Int) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	navigateToMediaScreen: () -> Unit,
	navigateToMediaItemScreen: (String, String) -> Unit,
	navigateToPlaylistsScreen: () -> Unit
) {
	val (currentMediaItem, _, songs, playlists) = uiState
	val openMenuSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	var longClickedSong by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val sheetState = rememberModalBottomSheetState(true)
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = { MainAppBar() },
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
		) {
			val menus = Menus.values()

			menus.forEach { menu ->
				val collectionName = stringResource(
					if (menu == Favorites) R.string.favorites else R.string.last_played
				)
				val action = { when (menu) {
					LastPlayed -> { navigateToMediaItemScreen(collectionName, LAST_PLAYED) }
					Playlist -> { navigateToPlaylistsScreen() }
					Favorites -> { navigateToMediaItemScreen(collectionName, FAVORITES) }
					Songs -> navigateToMediaScreen()
				} }
				MenuItem(menu.icon, menu.text, action)
			}

			if (songs.isNotEmpty()) {
				Spacer(Modifier.height(16.dp))
				Text(
					text = stringResource(R.string.recently_added),
					modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
					style = MaterialTheme.typography.titleLarge
				)
				LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
					itemsIndexed(
						items = songs,
						key = { _, song -> song.id }
					) { index, song ->
						RecentSongItem(index, song, currentMediaItem, playSong) {
							longClickedSong = song
							openMenuSheet.value = true
						}
					}
				}
			}
		}

		if (openMenuSheet.value && longClickedSong != null) {
			MediaItemSheet(
				song = longClickedSong!!,
				scaffoldState = sheetState,
				goToCollection = navigateToMediaItemScreen,
				openBottomSheet = { openMenuSheet.value = it },
				openAddToPlaylistDialog = { openAddToPlaylistDialog.value = true; openMenuSheet.value = false },
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, longClickedSong!!)
				}
			)
		}

		if (openAddToPlaylistDialog.value && longClickedSong != null) {
			AddToPlaylistDialog(
				songs = listOf(longClickedSong!!),
				playlists = playlists,
				addSongToPlaylist = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState)
				},
				openDialog = { openAddToPlaylistDialog.value = it }
			)
		}
	}
}

@Composable
fun MenuItem(icon: ImageVector?, text: Int, action: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(end = 40.dp)
			.clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
			.clickable { action() }
			.padding(horizontal = 30.dp, vertical = 20.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (icon != null) {
			Icon(imageVector = icon, contentDescription = null)
		}
		Text(
			text = stringResource(text),
			modifier = Modifier.padding(horizontal = 20.dp),
			style = MaterialTheme.typography.headlineSmall
		)
	}
}

private enum class Menus(val icon: ImageVector?, val text: Int) {
	LastPlayed(Icons.Rounded.History, R.string.last_played),
	Playlist(Icons.Rounded.QueueMusic, R.string.playlists),
	Favorites(Icons.Rounded.Favorite, R.string.favorites),
	Songs(Icons.Rounded.LibraryMusic, R.string.songs)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun LibraryScreenPreview() {
	OdiyoTheme {
		LibraryScreen(
			uiState = MediaUiState(songs = previewData.shuffled(), currentMediaItem = previewData[4].uri.toMediaItem),
			playSong = { _, _ -> },
			menuAction = {},
			navigateToMediaScreen = {},
			navigateToMediaItemScreen = { _, _ -> }
		) {}
	}
}