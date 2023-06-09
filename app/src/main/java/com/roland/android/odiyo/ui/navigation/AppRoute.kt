package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.*
import com.roland.android.odiyo.ui.components.BottomAppBar
import com.roland.android.odiyo.ui.screens.*
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel
import com.roland.android.odiyo.viewmodel.PlaylistViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppRoute(
	navActions: NavActions,
	navController: NavHostController,
	mediaViewModel: MediaViewModel,
	nowPlayingViewModel: NowPlayingViewModel,
	playlistViewModel: PlaylistViewModel
) {
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	var selectionModeClosed by remember { mutableStateOf(true) } // determines whether items in a LazyColumn are being selected.

	Scaffold(
		bottomBar = {
			BottomAppBar(
				song = mediaViewModel.currentSong,
				artwork = mediaViewModel.currentMediaItemImage,
				isPlaying = mediaViewModel.isPlaying,
				currentSongIndex = mediaViewModel.currentSongIndex,
				musicQueue = mediaViewModel.musicQueue,
				playlists = mediaViewModel.playlists,
				playPause = mediaViewModel::playAudio,
				queueAction = mediaViewModel::queueAction,
				menuAction = { mediaViewModel.menuAction(context, it) },
				moveToNowPlayingScreen = navActions::navigateToNowPlayingScreen,
				snackbarHostState = snackbarHostState,
				concealBottomBar = concealMinimizedView(navController),
				inSelectionMode = !selectionModeClosed
			)
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
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
					navigateToMediaItemScreen = navActions::navigateToMediaItemScreen,
					navigateToPlaylistsScreen = navActions::navigateToPlaylistScreen
				)
			}
			composable(AppRoute.MediaScreen.route) {
				MediaScreen(
					songsTab = { SongsTab(mediaViewModel, navActions) { selectionModeClosed = it } },
					albumsTab = { AlbumsTab(mediaViewModel, navActions) },
					artistsTab = { ArtistsTab(mediaViewModel, navActions) },
					inSelectMode = !selectionModeClosed,
					navigateToSearch = navActions::navigateToSearch,
					navigateUp = navController::navigateUp
				)
			}
			composable(AppRoute.PlaylistsScreen.route) {
				PlaylistsScreen(
					playlists = mediaViewModel.playlists,
					playlistAction = playlistViewModel::playlistActions,
					prepareAndViewSongs = navActions::navigateToMediaItemScreen,
					navigateUp = navController::navigateUp
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
					playlists = mediaViewModel.playlists,
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
					closeSelectionMode = { selectionModeClosed = it },
					goToCollection = navActions::navigateToMediaItemScreen,
					clearSearchQuery = { mediaViewModel.searchQuery = "" },
					closeSearchScreen = navController::navigateUp
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
				val collectionType = backStackEntry.arguments?.getString("collectionType")!!
				val songs = when (collectionType) {
					ALBUMS -> mediaViewModel.songsFromAlbum(collectionName)
					ARTISTS -> mediaViewModel.songsFromArtist(collectionName)
					FAVORITES -> mediaViewModel.favoriteSongs
					LAST_PLAYED -> mediaViewModel.lastPlayedSongs
					PLAYLISTS -> { mediaViewModel.fetchPlaylistSongs(collectionName); mediaViewModel.songsFromPlaylist }
					else -> emptyList()
				}

				MediaItemsScreen(
					songs = songs, collectionName = collectionName, collectionType = collectionType,
					currentSong = mediaViewModel.currentSong, playlists = mediaViewModel.playlists,
					sortOption = mediaViewModel.sortOrder,
					playAudio = { uri, index ->
						mediaViewModel.apply {
							resetPlaylist(songs)
							playAudio(uri, index)
						}
						index?.let { navActions.navigateToNowPlayingScreen() }
					},
					goToCollection = navActions::navigateToMediaItemScreen,
					menuAction = { mediaViewModel.menuAction(context, it) },
					closeSelectionMode = { selectionModeClosed = it },
					moveToAddSongsScreen = navActions::navigateToAddSongsScreen,
					navigateUp = navController::navigateUp
				)
			}
			composable(AppRoute.AddSongsScreen.route) { backStackEntry ->
				val playlistName = backStackEntry.arguments?.getString("playlistToAddTo")

				AddSongsScreen(
					songs = mediaViewModel.songsToAddToPlaylist(playlistName),
					playlists = mediaViewModel.playlists, playlistToAddTo = playlistName,
					menuAction = { mediaViewModel.menuAction(context, it) }
				) { if (it) navController.navigateUp(); selectionModeClosed = it }
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
					repeatMode = nowPlayingViewModel.repeatMode,
					shuffleState = nowPlayingViewModel.shuffleState,
					progress = nowPlayingViewModel.seekProgress,
					timeElapsed = nowPlayingViewModel.currentDuration,
					currentSongIndex = mediaViewModel.currentSongIndex,
					musicQueue = nowPlayingViewModel.musicQueue,
					playlists = nowPlayingViewModel.playlists,
					mediaControl = { nowPlayingViewModel.mediaControl(context, it) },
					queueAction = nowPlayingViewModel::queueAction,
					goToCollection = navActions::navigateToMediaItemScreen,
					navigateUp = navController::navigateUp
				)
			}
		}
	}
}