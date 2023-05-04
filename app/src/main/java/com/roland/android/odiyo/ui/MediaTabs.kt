package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.roland.android.odiyo.viewmodel.MediaViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryTab(viewModel: MediaViewModel, navController: NavHostController) {
	LibraryScreen(
		songs = viewModel.songs,
		currentSong = viewModel.currentSong,
		playAudio = { uri, index ->
			viewModel.playAudio(uri, index)
			index?.let { navController.navigate(AppRoute.NowPlayingScreen.route) }
		},
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AlbumsTab(viewModel: MediaViewModel, navController: NavHostController) {
	AlbumsScreen(
		albums = viewModel.albumList,
		prepareSongs = {
			viewModel.albumName = it
			navController.navigate(AppRoute.MediaItemsScreen.route)
		}
	)
}

@Composable
fun ArtistsTab() {
	ArtistsScreen()
}