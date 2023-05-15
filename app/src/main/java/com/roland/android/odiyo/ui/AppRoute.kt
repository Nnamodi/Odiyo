package com.roland.android.odiyo.ui

import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AppRoute(
	navActions: NavActions,
	navController: NavHostController,
	mediaViewModel: MediaViewModel,
	nowPlayingViewModel: NowPlayingViewModel,
	audioManager: AudioManager
) {
	Scaffold(
		bottomBar = {
			BottomAppBar(
				concealBottomBar = currentRoute(navController = navController) != AppRoute.NowPlayingScreen.route,
				song = mediaViewModel.currentSong,
				artwork = mediaViewModel.currentMediaItemImage,
				isPlaying = mediaViewModel.isPlaying,
				playPause = mediaViewModel::playAudio,
				moveToNowPlayingScreen = { navActions.navigateToNowPlayingScreen() }
			)
		}
	) { innerPadding ->
		NavHost(
			navController = navController,
			startDestination = AppRoute.MediaScreen.route,
			modifier = Modifier.padding(innerPadding)
		) {
			composable(AppRoute.MediaScreen.route) {
				MediaScreen(
					libraryTab = { LibraryTab(mediaViewModel, navActions) },
					albumsTab = { AlbumsTab(mediaViewModel, navActions) },
					artistsTab = { ArtistsTab(mediaViewModel, navActions) },
					navigateToSearch = { navActions.navigateToSearch() }
				)
			}
			composable(AppRoute.SearchScreen.route) {
				SearchScreen(
					searchQuery = mediaViewModel.searchQuery,
					searchResult = mediaViewModel.songsFromSearch(),
					onTextChange = { mediaViewModel.searchQuery = it },
					currentSong = mediaViewModel.currentSong,
					playAudio = { uri, index ->
						mediaViewModel.mediaItems = mediaViewModel.songsFromSearch().map { it.uri.toMediaItem }
						mediaViewModel.playAudio(uri, index)
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = mediaViewModel::menuAction,
					clearSearchQuery = { mediaViewModel.searchQuery = "" },
					closeSearchScreen = { navController.navigateUp() }
				)
			}
			composable(
				route = AppRoute.MediaItemsScreen.route,
				arguments = listOf(
					navArgument("collectionName") { type = NavType.StringType },
					navArgument("collectionType") { type = NavType.StringType }
				)
			) { backStackEntry ->
				val collectionName = backStackEntry.arguments?.getString("collectionName")!!
				val songs = when (backStackEntry.arguments?.getString("collectionType")!!) {
					"albums" -> mediaViewModel.songsFromAlbum(collectionName)
					"artists" -> mediaViewModel.songsFromArtist(collectionName)
					else -> emptyList()
				}

				MediaItemsScreen(
					songs = songs,
					collectionName = collectionName,
					currentSong = mediaViewModel.currentSong,
					playAudio = { uri, index ->
						mediaViewModel.mediaItems = songs.map { it.uri.toMediaItem }
						mediaViewModel.playAudio(uri, index)
						index?.let { navActions.navigateToNowPlayingScreen() }
					},
					menuAction = mediaViewModel::menuAction,
					navigateUp = { navController.navigateUp() }
				)
			}
			composable(AppRoute.NowPlayingScreen.route) {
				NowPlayingScreen(
					song = nowPlayingViewModel.currentSong,
					artwork = nowPlayingViewModel.currentMediaItemImage,
					isPlaying = nowPlayingViewModel.isPlaying,
					deviceMuted = nowPlayingViewModel.isDeviceMuted,
					onShuffle = nowPlayingViewModel.shuffleState,
					progress = nowPlayingViewModel.seekProgress,
					timeElapsed = nowPlayingViewModel.currentDuration,
					onSeekToPosition = nowPlayingViewModel::onSeekToPosition,
					playPause = nowPlayingViewModel::playAudio,
					shuffle = nowPlayingViewModel::shuffle,
					seekTo = nowPlayingViewModel::seek,
					muteDevice = { nowPlayingViewModel.onMuteDevice(audioManager) },
					navigateUp = { navController.navigateUp() }
				)
			}
		}
	}
}