package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
	inSelectionMode: (Boolean) -> Unit
) {
	val context = LocalContext.current

	SongsScreen(
		uiState = viewModel.mediaScreenUiState,
		playAudio = { uri, index ->
			viewModel.apply {
				resetPlaylist(viewModel.mediaScreenUiState.songs)
				playAudio(uri, index)
			}
			index?.let { navActions.navigateToNowPlayingScreen() }
		},
		goToCollection = navActions::navigateToMediaItemScreen,
		menuAction = { viewModel.menuAction(context, it) },
		inSelectionMode = inSelectionMode
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AlbumsTab(viewModel: MediaViewModel, navActions: NavActions) {
	AlbumsScreen(
		albums = viewModel.mediaScreenUiState.albums,
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
		artists = viewModel.mediaScreenUiState.artists,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, ARTISTS)
		}
	)
}