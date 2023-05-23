package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.ui.screens.AlbumsScreen
import com.roland.android.odiyo.ui.screens.ArtistsScreen
import com.roland.android.odiyo.ui.screens.LibraryScreen
import com.roland.android.odiyo.viewmodel.MediaViewModel

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryTab(viewModel: MediaViewModel, navActions: NavActions) {
	val context = LocalContext.current

	LibraryScreen(
		songs = viewModel.songs,
		currentSong = viewModel.currentSong,
		playAudio = { uri, index ->
			viewModel.apply {
				resetPlaylist(songs)
				playAudio(uri, index)
			}
			index?.let { navActions.navigateToNowPlayingScreen() }
		},
		menuAction = { viewModel.menuAction(context, it) }
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AlbumsTab(viewModel: MediaViewModel, navActions: NavActions) {
	AlbumsScreen(
		albums = viewModel.albumList,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, "albums")
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
			navActions.navigateToMediaItemScreen(it, "artists")
		}
	)
}