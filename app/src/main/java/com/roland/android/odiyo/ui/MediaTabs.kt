package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.roland.android.odiyo.viewmodel.OdiyoViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryTab(viewModel: OdiyoViewModel, navController: NavHostController) {
	LibraryScreen(
		songs = viewModel.songs,
		currentSong = viewModel.currentSong,
		playAudio = { uri, index ->
			viewModel.playAudio(uri, index)
			index?.let { navController.navigate(AppRoute.NowPlayingScreen.route) }
		},
	)
}

@Composable
fun AlbumsTab() {
	AlbumsScreen()
}

@Composable
fun ArtistsTab() {
	ArtistsScreen()
}