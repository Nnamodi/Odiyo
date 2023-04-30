package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.roland.android.odiyo.service.Util.convertToBitmap
import com.roland.android.odiyo.service.Util.getArtwork
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
				currentMediaItemArtwork = viewModel.nowPlayingMetaData?.convertToBitmap() ?: viewModel.currentSong?.getArtwork(),
				currentSong = viewModel.currentSong,
				isPlaying = viewModel.isPlaying,
				playAudio = { uri, moveToNowPlaying ->
					viewModel.playAudio(uri, moveToNowPlaying)
					if (moveToNowPlaying) { navController.navigate(AppRoute.NowPlayingScreen.route) }
				},
				moveToNowPlayingScreen = { navController.navigate(AppRoute.NowPlayingScreen.route) }
			)
		}
		composable(AppRoute.NowPlayingScreen.route) {
			NowPlayingScreen(
				song = viewModel.currentSong,
				artwork = viewModel.nowPlayingMetaData?.convertToBitmap() ?: viewModel.currentSong?.getArtwork(),
				isPlaying = viewModel.isPlaying,
				deviceMuted = viewModel.isDeviceMuted,
				onShuffle = viewModel.shuffleState,
				progress = viewModel.progress,
				playPause = viewModel::playAudio,
				shuffle = viewModel::shuffle,
				seekTo = viewModel::seek,
				muteDevice = viewModel::onMuteDevice,
				navigateUp = { navController.navigateUp() }
			)
		}
	}
}

sealed class AppRoute(val route: String) {
	object MediaScreen: AppRoute("media_screen")
	object NowPlayingScreen: AppRoute("now_playing_screen")
}