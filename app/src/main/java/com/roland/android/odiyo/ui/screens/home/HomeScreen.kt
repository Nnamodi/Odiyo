package com.roland.android.odiyo.ui.screens.home

import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryMusic
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
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.ui.components.RecentSongItem
import com.roland.android.odiyo.ui.components.appbars.MainAppBar
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.navigation.FAVORITES
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.navigation.RECENTLY_ADDED
import com.roland.android.odiyo.ui.navigation.Screens
import com.roland.android.odiyo.ui.screens.CommonScreen
import com.roland.android.odiyo.ui.screens.LoadingRowUi
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.actions.MediaMenuActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	uiState: HomeUiState,
	menuAction: (MediaMenuActions) -> Unit,
	navigate: (Screens) -> Unit
) {
	val (recentlyAddedSongs, playlists, currentMediaItem) = uiState
	val openMenuSheet = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	var longClickedSong by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val sheetState = rememberModalBottomSheetState(true)
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = { MainAppBar { navigate(Screens.SettingsScreen) } },
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
			Menus.entries.forEach { menu ->
				val collectionName = stringResource(
					if (menu == Menus.Favorites) R.string.favorites else R.string.last_played
				)
				val action = { when (menu) {
					Menus.LastPlayed -> { navigate(Screens.ListScreen(collectionName, LAST_PLAYED)) }
					Menus.Playlist -> { navigate(Screens.PlaylistsScreen) }
					Menus.Favorites -> { navigate(Screens.ListScreen(collectionName, FAVORITES)) }
					Menus.Songs -> navigate(Screens.MediaScreen)
				} }
				MenuItem(menu.icon, menu.text, action)
			}

			if (recentlyAddedSongs is State.Success && recentlyAddedSongs.data.isNotEmpty()) {
				Spacer(Modifier.height(16.dp))
				Text(
					text = stringResource(R.string.recently_added),
					modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
					style = MaterialTheme.typography.titleLarge
				)
			}
			CommonScreen(
				state = recentlyAddedSongs,
				loadingScreen = { LoadingRowUi() }
			) { songs ->
				LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
					itemsIndexed(
						items = songs,
						key = { _, song -> song.id }
					) { index, song ->
						RecentSongItem(
							modifier = Modifier.animateItem(
								fadeInSpec = null,
								fadeOutSpec = null,
								placementSpec = tween(1000)
							),
							song = song,
							currentMediaItem = currentMediaItem,
							playSong = {
								val nowPlayingFrom = NowPlayingFrom(RECENTLY_ADDED, PLAYLISTS)
								menuAction(MediaMenuActions.PlayAudio(song.uri, index, songs, nowPlayingFrom))
							}
						) {
							longClickedSong = song
							openMenuSheet.value = true
						}
					}
				}
			}
			Spacer(Modifier.padding(bottom = 100.dp))
		}

		if (openMenuSheet.value && longClickedSong != null) {
			MediaItemSheet(
				song = longClickedSong!!,
				scaffoldState = sheetState,
				goToCollection = { collectionName, collectionType ->
					navigate(Screens.ListScreen(collectionName, collectionType))
				},
				openBottomSheet = { openMenuSheet.value = it },
				openAddToPlaylistDialog = { openAddToPlaylistDialog.value = true },
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
private fun MenuItem(icon: ImageVector?, text: Int, action: () -> Unit) {
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
	Playlist(Icons.AutoMirrored.Rounded.QueueMusic, R.string.playlists),
	Favorites(Icons.Rounded.Favorite, R.string.favorites),
	Songs(Icons.Rounded.LibraryMusic, R.string.songs)
}

@Preview
@Composable
private fun HomeScreenPreview() {
	OdiyoTheme {
		HomeScreen(
			uiState = HomeUiState(),
			menuAction = {},
			navigate = {}
		)
	}
}

@Preview(device = "spec:parent=pixel_3,orientation=landscape")
@Composable
private fun HomeScreenLandscapePreview() {
	HomeScreenPreview()
}