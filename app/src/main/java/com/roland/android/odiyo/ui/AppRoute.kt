package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.roland.android.odiyo.viewmodel.OdiyoViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AppRoute(
	navController: NavHostController,
	viewModel: OdiyoViewModel
) {
	NavHost(
		navController = navController,
		startDestination = AppRoute.MediaScreen.route
	) {
		composable(AppRoute.MediaScreen.route) {
			MediaScreen(
				songs = viewModel.songs,
				currentSong = viewModel.currentSong,
				isPlaying = viewModel.isPlaying,
				playAudio = { uri, moveToNowPlaying ->
					viewModel.playAudio(uri)
					if (moveToNowPlaying) { navController.navigate(AppRoute.NowPlayingScreen.route) }
				},
				moveToNowPlayingScreen = { navController.navigate(AppRoute.NowPlayingScreen.route) }
			)
		}
		composable(AppRoute.NowPlayingScreen.route) {
			NowPlayingScreen(
				song = viewModel.currentSong,
				isPlaying = viewModel.isPlaying,
				progress = viewModel.progress,
				playPause = viewModel::playAudio,
				seekTo = viewModel::seek,
				navigateUp = { navController.navigateUp() }
			)
		}
	}
}

sealed class AppRoute(val route: String) {
	object MediaScreen: AppRoute("media_screen")
	object NowPlayingScreen: AppRoute("now_playing_screen")
}