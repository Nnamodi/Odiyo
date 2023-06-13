package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.ui.screens.AlbumsScreen
import com.roland.android.odiyo.ui.screens.ArtistsScreen
import com.roland.android.odiyo.ui.screens.SongsScreen
import com.roland.android.odiyo.viewmodel.MediaViewModel

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun SongsTab(
	viewModel: MediaViewModel,
	navActions: NavActions,
	selectedSongs: MutableState<Set<Long>>,
	inSelectionMode: (Set<Long>) -> Unit
) {
	val context = LocalContext.current

	SongsScreen(
		songs = viewModel.songs,
		currentSong = viewModel.currentSong,
		playlists = viewModel.playlists,
		sortOption = viewModel.sortOrder,
		playAudio = { uri, index ->
			viewModel.apply {
				resetPlaylist(songs)
				playAudio(uri, index)
			}
			index?.let { navActions.navigateToNowPlayingScreen() }
		},
		goToCollection = navActions::navigateToMediaItemScreen,
		menuAction = { viewModel.menuAction(context, it) },
		selectedSongs = selectedSongs,
	) { inSelectionMode(it) }
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AlbumsTab(viewModel: MediaViewModel, navActions: NavActions) {
	AlbumsScreen(
		albums = viewModel.albumList,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, ALBUMS)
		}
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun ArtistsTab(viewModel: MediaViewModel, navActions: NavActions) {
	ArtistsScreen(
		artists = viewModel.artistList,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, ARTISTS)
		}
	)
}