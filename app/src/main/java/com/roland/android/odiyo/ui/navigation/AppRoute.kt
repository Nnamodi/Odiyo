package com.roland.android.odiyo.ui.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.*
import com.roland.android.odiyo.ui.screens.BottomAppBar
import com.roland.android.odiyo.ui.screens.MediaItemsScreen
import com.roland.android.odiyo.ui.screens.MediaScreen
import com.roland.android.odiyo.ui.screens.SearchScreen
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
) {
	val context = LocalContext.current

	Scaffold(
		bottomBar = {
			BottomAppBar(
				song = mediaViewModel.currentSong,
				artwork = mediaViewModel.currentMediaItemImage,
				isPlaying = mediaViewModel.isPlaying,
				currentSongIndex = mediaViewModel.currentSongIndex,
				musicQueue = mediaViewModel.musicQueue,
				playPause = mediaViewModel::playAudio,
				queueAction = mediaViewModel::queueAction,
				moveToNowPlayingScreen = { navActions.navigateToNowPlayingScreen() },
				concealBottomBar = currentRoute(navController = navController) != AppRoute.NowPlayingScreen.route
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
						mediaViewModel.apply {
							resetPlaylist(songsFromSearch())
							playAudio(uri, index)
						}
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
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
						mediaViewModel.apply {
							resetPlaylist(songs)
							playAudio(uri, index)
						}
						index?.let { navActions.navigateToNowPlayingScreen() }
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
					navigateUp = { navController.navigateUp() }
				)
			}
			composable(AppRoute.NowPlayingScreen.route) {
				NowPlayingScreen(
					song = nowPlayingViewModel.currentSong,
					artwork = nowPlayingViewModel.currentMediaItemImage,
					isPlaying = nowPlayingViewModel.isPlaying,
					deviceMuted = nowPlayingViewModel.isDeviceMuted,
					shuffleState = nowPlayingViewModel.shuffleState,
					progress = nowPlayingViewModel.seekProgress,
					timeElapsed = nowPlayingViewModel.currentDuration,
					currentSongIndex = mediaViewModel.currentSongIndex,
					musicQueue = nowPlayingViewModel.musicQueue,
					mediaControl = { nowPlayingViewModel.mediaControl(context, it) },
					queueAction = nowPlayingViewModel::queueAction,
					navigateUp = { navController.navigateUp() }
				)
			}
		}
	}
}