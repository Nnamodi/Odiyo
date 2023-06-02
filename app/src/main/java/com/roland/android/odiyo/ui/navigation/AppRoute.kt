package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.*
import com.roland.android.odiyo.ui.navigation.NavAnimations.DOWN
import com.roland.android.odiyo.ui.navigation.NavAnimations.LEFT
import com.roland.android.odiyo.ui.navigation.NavAnimations.RIGHT
import com.roland.android.odiyo.ui.navigation.NavAnimations.UP
import com.roland.android.odiyo.ui.screens.*
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel

@ExperimentalAnimationApi
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
				concealBottomBar = concealMinimizedView(navController)
			)
		}
	) { innerPadding ->
		AnimatedNavHost(
			navController = navController,
			startDestination = AppRoute.LibraryScreen.route,
			modifier = Modifier.padding(innerPadding)
		) {
			composable(AppRoute.LibraryScreen.route) {
				LibraryScreen(
					songs = mediaViewModel.recentSongs,
					currentSongUri = mediaViewModel.currentSong?.uri?.toMediaItem ?: NOTHING_PLAYING,
					playSong = { uri, index ->
						mediaViewModel.apply {
							resetPlaylist(recentSongs)
							playAudio(uri, index)
						}
						navActions.navigateToNowPlayingScreen()
					},
					navigateToMediaScreen = navActions::navigateToMediaScreen,
					navigateToMediaItemScreen = { collectionName, collectionType ->
						navActions.navigateToMediaItemScreen(collectionName, collectionType)
					}
				)
			}
			composable(AppRoute.MediaScreen.route) {
				MediaScreen(
					songsTab = { SongsTab(mediaViewModel, navActions) },
					albumsTab = { AlbumsTab(mediaViewModel, navActions) },
					artistsTab = { ArtistsTab(mediaViewModel, navActions) },
					navigateToSearch = { navActions.navigateToSearch() },
					navigateUp = { navController.navigateUp() }
				)
			}
			composable(
				AppRoute.SearchScreen.route,
				enterTransition = { slideIntoContainer(LEFT, tween(700)) },
				exitTransition = null,
				popEnterTransition = null,
				popExitTransition = { slideOutOfContainer(RIGHT, tween(700)) }
			) {
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
					goToCollection = navActions::navigateToMediaItemScreen,
					clearSearchQuery = { mediaViewModel.searchQuery = "" },
					closeSearchScreen = { navController.navigateUp() }
				)
			}
			composable(
				route = AppRoute.MediaItemsScreen.route,
				arguments = listOf(
					navArgument("collectionName") { type = NavType.StringType },
					navArgument("collectionType") { type = NavType.StringType }
				),
				enterTransition = { slideIntoContainer(LEFT, tween(700)) },
				exitTransition = null,
				popEnterTransition = null,
				popExitTransition = { slideOutOfContainer(RIGHT, tween(700)) }
			) { backStackEntry ->
				val collectionName = backStackEntry.arguments?.getString("collectionName")!!
				val songs = when (backStackEntry.arguments?.getString("collectionType")!!) {
					ALBUMS -> mediaViewModel.songsFromAlbum(collectionName)
					ARTISTS -> mediaViewModel.songsFromArtist(collectionName)
					FAVORITES -> mediaViewModel.favoriteSongs
					LAST_PLAYED -> mediaViewModel.lastPlayedSongs
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
					goToCollection = navActions::navigateToMediaItemScreen,
					menuAction = { mediaViewModel.menuAction(context, it) },
					navigateUp = { navController.navigateUp() }
				)
			}
			composable(
				AppRoute.NowPlayingScreen.route,
				enterTransition = { slideIntoContainer(UP, tween(700)) },
				exitTransition = null,
				popEnterTransition = null,
				popExitTransition = { slideOutOfContainer(DOWN, tween(700)) }
			) {
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
					goToCollection = navActions::navigateToMediaItemScreen,
					navigateUp = { navController.navigateUp() }
				)
			}
		}
	}
}