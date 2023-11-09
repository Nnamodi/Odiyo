package com.roland.android.odiyo.ui.screens

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
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
import com.roland.android.odiyo.ui.screens.Menus.Favorites
import com.roland.android.odiyo.ui.screens.Menus.LastPlayed
import com.roland.android.odiyo.ui.screens.Menus.Playlist
import com.roland.android.odiyo.ui.screens.Menus.Songs
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
	uiState: MediaUiState,
	playSong: (Uri, Int) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	navigateToMediaScreen: () -> Unit,
	navigateToMediaItemScreen: (String, String) -> Unit,
	navigateToPlaylistsScreen: () -> Unit,
	navigateToSettingsScreen: () -> Unit
) {
	val (currentMediaItem, _, _, recentSongs, playlists) = uiState
	val openMenuSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	var longClickedSong by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val sheetState = rememberModalBottomSheetState(true)
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = { MainAppBar(navigateToSettingsScreen) },
		snackbarHost = {
			SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = (-80).dp)) {
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
			Menus.values().forEach { menu ->
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

			if (recentSongs.isNotEmpty() || uiState.isLoading) {
				Spacer(Modifier.height(16.dp))
				Text(
					text = stringResource(R.string.recently_added),
					modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
					style = MaterialTheme.typography.titleLarge
				)
				if (uiState.isLoading) {
					LoadingRowUi()
				} else {
					LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
						itemsIndexed(
							items = recentSongs,
							key = { _, song -> song.id }
						) { index, song ->
							RecentSongItem(
								modifier = Modifier.animateItemPlacement(tween(1000)),
								itemIndex = index,
								song = song,
								currentMediaItem = currentMediaItem ?: MediaItem.EMPTY,
								playSong = playSong
							) {
								longClickedSong = song
								openMenuSheet.value = true
							}
						}
					}
				}
				Spacer(Modifier.padding(bottom = 100.dp))
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

@Preview
@Composable
private fun LibraryScreenPreview() {
	OdiyoTheme {
		LibraryScreen(
			uiState = MediaUiState(currentMediaItem = previewData[4].uri.toMediaItem, recentSongs = previewData.shuffled()),
			playSong = { _, _ -> },
			menuAction = {},
			navigateToMediaScreen = {},
			navigateToMediaItemScreen = { _, _ -> },
			navigateToPlaylistsScreen = {}
		) {}
	}
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape")
@Composable
private fun LibraryScreenLandscapePreview() {
	LibraryScreenPreview()
}