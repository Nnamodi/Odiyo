package com.roland.android.odiyo.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.roland.android.odiyo.ui.components.BottomAppBar
import com.roland.android.odiyo.ui.screens.*
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel
import com.roland.android.odiyo.viewmodel.PlaylistViewModel
import com.roland.android.odiyo.viewmodel.SettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppRoute(
	navActions: NavActions,
	navController: NavHostController,
	mediaViewModel: MediaViewModel,
	nowPlayingViewModel: NowPlayingViewModel,
	playlistViewModel: PlaylistViewModel,
	settingsViewModel: SettingsViewModel
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
					playSong = { uri, index, collectionType, collectionName ->
						mediaViewModel.apply {
							resetPlaylist(recentSongs)
							playAudio(uri, index, collectionType, collectionName)
						}
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
					navigateToMediaScreen = navActions::navigateToMediaScreen,
					navigateToMediaItemScreen = navActions::navigateToMediaItemScreen,
					navigateToPlaylistsScreen = navActions::navigateToPlaylistScreen,
					navigateToSettingsScreen = navActions::navigateToSettingsScreen
				)
			}
			composableI(AppRoute.MediaScreen.route) {
				MediaScreen(
					songsTab = { SongsTab(mediaViewModel, navActions) { selectionModeClosed = it } },
					albumsTab = { AlbumsTab(mediaViewModel, navActions) },
					artistsTab = { ArtistsTab(mediaViewModel, navActions) },
					inSelectMode = !selectionModeClosed,
					navigateToSearch = navActions::navigateToSearch,
					navigateUp = navController::navigateUp
				)
			}
			composableI(AppRoute.PlaylistsScreen.route) {
				PlaylistsScreen(
					playlists = mediaViewModel.mediaScreenUiState.playlists,
					playlistAction = playlistViewModel::playlistActions,
					prepareAndViewSongs = navActions::navigateToMediaItemScreen,
					navigateUp = navController::navigateUp
				)
			}
			composableI(AppRoute.SettingsScreen.route) {
				SettingsScreen(
					uiState = settingsViewModel.settingsScreenUiState,
					settingsAction = settingsViewModel::settingsAction,
					navigateToAboutUsScreen = navActions::navigateToAboutUsScreen,
					navigateUp = navController::navigateUp
				)
			}
			composableI(AppRoute.AboutUsScreen.route) { backStackEntry ->
				val screenToShow = backStackEntry.arguments?.getString("screenToShow") ?: ""

				AboutUsScreen(screenToShow = screenToShow, navigateUp = navController::navigateUp)
			}
			composableI(AppRoute.SearchScreen.route) {
				SearchScreen(
					uiState = mediaViewModel.mediaItemsScreenUiState,
					onSearch = mediaViewModel::onSearch,
					playAudio = { uri, index, collectionType, collectionName ->
						mediaViewModel.apply {
							resetPlaylist(mediaItemsScreenUiState.songs)
							playAudio(uri, index, collectionType, collectionName)
						}
						navActions.navigateToNowPlayingScreen()
					},
					menuAction = { mediaViewModel.menuAction(context, it) },
					closeSelectionMode = { selectionModeClosed = it },
					goToCollection = navActions::navigateToMediaItemScreen,
					closeSearchScreen = navController::navigateUp
				)
			}
			composableI(
				route = AppRoute.MediaItemsScreen.route,
				arguments = listOf(
					navArgument("collectionName") { type = NavType.StringType },
					navArgument("collectionType") { type = NavType.StringType }
				)
			) { backStackEntry ->
				val collectionName = backStackEntry.arguments?.getString("collectionName") ?: ""
				val collectionType = backStackEntry.arguments?.getString("collectionType") ?: ""
				val previousScreenIsNowPlayingScreen = navController.previousBackStackEntry?.destination?.route == AppRoute.NowPlayingScreen.route
				mediaViewModel.prepareMediaItems(collectionType, collectionName)

				MediaItemsScreen(
					uiState = mediaViewModel.mediaItemsScreenUiState,
					previousScreenIsNowPlayingScreen = previousScreenIsNowPlayingScreen,
					playAudio = { uri, index, type, name ->
						mediaViewModel.apply {
							resetPlaylist(mediaItemsScreenUiState.songs)
							playAudio(uri, index, type, name)
						}
						if (previousScreenIsNowPlayingScreen) return@MediaItemsScreen
						navActions.navigateToNowPlayingScreen()
					},
					goToCollection = navActions::navigateToMediaItemScreen,
					menuAction = { mediaViewModel.menuAction(context, it) },
					closeSelectionMode = { selectionModeClosed = it },
					moveToAddSongsScreen = navActions::navigateToAddSongsScreen,
					navigateUp = navController::navigateUp
				)
			}
			composableI(AppRoute.AddSongsScreen.route) { backStackEntry ->
				val playlistName = backStackEntry.arguments?.getString("playlistToAddTo") ?: ""
				mediaViewModel.prepareMediaItems(ADD_TO_PLAYLIST, playlistName)

				AddSongsScreen(
					uiState = mediaViewModel.mediaItemsScreenUiState,
					menuAction = { mediaViewModel.menuAction(context, it) }
				) { if (it) navController.navigateUp(); selectionModeClosed = it }
			}
			composableII(AppRoute.NowPlayingScreen.route) {
				NowPlayingScreen(
					uiState = nowPlayingViewModel.nowPlayingScreenUiState,
					isDarkTheme = settingsViewModel.isDarkTheme ?: isSystemInDarkTheme(),
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