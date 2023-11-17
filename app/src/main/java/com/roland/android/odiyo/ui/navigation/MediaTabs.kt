package com.roland.android.odiyo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.roland.android.odiyo.ui.screens.AlbumsScreen
import com.roland.android.odiyo.ui.screens.ArtistsScreen
import com.roland.android.odiyo.ui.screens.SongsScreen
import com.roland.android.odiyo.viewmodel.MediaViewModel

@Composable
fun SongsTab(
	viewModel: MediaViewModel,
	navActions: NavActions,
	inSelectionMode: (Boolean) -> Unit
) {
	val context = LocalContext.current

	SongsScreen(
		uiState = viewModel.mediaScreenUiState,
		playAudio = { uri, index, collectionType, collectionName ->
			viewModel.apply {
				resetPlaylist(viewModel.mediaScreenUiState.allSongs)
				playAudio(uri, index, collectionType, collectionName)
			}
			index?.let { navActions.navigateToNowPlayingScreen() }
		},
		goToCollection = navActions::navigateToMediaItemScreen,
		menuAction = { viewModel.menuAction(context, it) },
		closeSelectionMode = inSelectionMode
	)
}

@Composable
fun AlbumsTab(viewModel: MediaViewModel, navActions: NavActions) {
	AlbumsScreen(
		uiState = viewModel.mediaScreenUiState,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, ALBUMS)
		}
	)
}

@Composable
fun ArtistsTab(viewModel: MediaViewModel, navActions: NavActions) {
	ArtistsScreen(
		uiState = viewModel.mediaScreenUiState,
		prepareAndViewSongs = {
			navActions.navigateToMediaItemScreen(it, ARTISTS)
		}
	)
}