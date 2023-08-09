package com.roland.android.odiyo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
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
				uiState = mediaViewModel.nowPlayingScreenUiState,
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
			SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = (34).dp)) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) {
		AnimatedNavHost(
			navController = navController,
			startDestination = AppRoute.LibraryScreen.route
		) {
			composable(AppRoute.LibraryScreen.route) {
				LibraryScreen(
					uiState = mediaViewModel.mediaScreenUiState,
					playSong = { uri, index ->
						mediaViewModel.apply {
							resetPlaylist(recentSongs)
							playAudio(uri, index)
						}
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
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
					playlists = mediaViewModel.mediaScreenUiState.playlists,
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
					uiState = mediaViewModel.mediaItemsScreenUiState,
					editSearchQuery = mediaViewModel::editSearchQuery,
					playAudio = { uri, index ->
						mediaViewModel.apply {
							resetPlaylist(mediaItemsScreenUiState.songs)
							playAudio(uri, index)
						}
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
					closeSelectionMode = { selectionModeClosed = it },
					goToCollection = navActions::navigateToMediaItemScreen,
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
				val collectionName = backStackEntry.arguments?.getString("collectionName") ?: ""
				val collectionType = backStackEntry.arguments?.getString("collectionType") ?: ""
				mediaViewModel.prepareMediaItems(collectionName, collectionType)

				MediaItemsScreen(
					uiState = mediaViewModel.mediaItemsScreenUiState,
					playAudio = { uri, index ->
						mediaViewModel.apply {
							resetPlaylist(mediaItemsScreenUiState.songs)
							playAudio(uri, index)
						}
						navActions.navigateToNowPlayingScreen()
					},
					goToCollection = navActions::navigateToMediaItemScreen,
					menuAction = { mediaViewModel.menuAction(context, it) },
					closeSelectionMode = { selectionModeClosed = it },
					moveToAddSongsScreen = navActions::navigateToAddSongsScreen,
					navigateUp = navController::navigateUp
				)
			}
			composable(AppRoute.AddSongsScreen.route) { backStackEntry ->
				val playlistName = backStackEntry.arguments?.getString("playlistToAddTo") ?: ""
				mediaViewModel.prepareMediaItems(playlistName, ADD_TO_PLAYLIST)

				AddSongsScreen(
					uiState = mediaViewModel.mediaItemsScreenUiState,
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
					uiState = nowPlayingViewModel.nowPlayingScreenUiState,
					mediaControl = { nowPlayingViewModel.mediaControl(context, it) },
					menuAction = { mediaViewModel.menuAction(context, it) },
					queueAction = nowPlayingViewModel::queueAction,
					goToCollection = navActions::navigateToMediaItemScreen,
					navigateUp = navController::navigateUp
				)
			}
		}
	}
}