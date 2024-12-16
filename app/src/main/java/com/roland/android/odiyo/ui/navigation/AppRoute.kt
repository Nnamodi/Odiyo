package com.roland.android.odiyo.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.roland.android.odiyo.ui.components.appbars.BottomAppBar
import com.roland.android.odiyo.ui.screens.home.HomeScreen
import com.roland.android.odiyo.ui.screens.home.HomeViewModel
import com.roland.android.odiyo.ui.screens.list.AddSongsScreen
import com.roland.android.odiyo.ui.screens.list.ListScreen
import com.roland.android.odiyo.ui.screens.list.ListViewModel
import com.roland.android.odiyo.ui.screens.media.MediaScreen
import com.roland.android.odiyo.ui.screens.media.MediaViewModel
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.MediaControls
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingScreen
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingViewModel
import com.roland.android.odiyo.ui.screens.playlists.PlaylistViewModel
import com.roland.android.odiyo.ui.screens.playlists.PlaylistsScreen
import com.roland.android.odiyo.ui.screens.search.SearchScreen
import com.roland.android.odiyo.ui.screens.search.SearchViewModel
import com.roland.android.odiyo.ui.screens.settings.AboutUsScreen
import com.roland.android.odiyo.ui.screens.settings.SettingsScreen
import com.roland.android.odiyo.ui.screens.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppRoute(
	navActions: NavActions,
	navController: NavHostController,
	homeViewModel: HomeViewModel = koinViewModel(),
	listViewModel: ListViewModel = koinViewModel(),
	mediaViewModel: MediaViewModel = koinViewModel(),
	nowPlayingViewModel: NowPlayingViewModel = koinViewModel(),
	playlistViewModel: PlaylistViewModel = koinViewModel(),
	searchViewModel: SearchViewModel = koinViewModel(),
	settingsViewModel: SettingsViewModel = koinViewModel()
) {
	val snackbarHostState = remember { SnackbarHostState() }
	var selectionModeClosed by remember { mutableStateOf(true) } // determines whether items in a LazyColumn are being selected.

	Scaffold(
		bottomBar = {
			BottomAppBar(
				uiState = nowPlayingViewModel.nowPlayingUiState,
				playPause = { nowPlayingViewModel.mediaControl(MediaControls.PlayPause) },
				queueAction = nowPlayingViewModel::queueActions,
				menuAction = mediaViewModel::menuAction,
				moveToNowPlayingScreen = { navActions.navigate(Screens.NowPlayingScreen) },
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
		NavHost(
			navController = navController,
			startDestination = AppRoute.HomeScreen.route
		) {
			composable(AppRoute.HomeScreen.route) {
				HomeScreen(
					uiState = homeViewModel.homeUiState,
					menuAction = mediaViewModel::menuAction,
					navigate = navActions::navigate
				)
			}
			composableI(AppRoute.MediaScreen.route) {
				MediaScreen(
					uiState = mediaViewModel.mediaUiState,
					inSelectMode = !selectionModeClosed,
					menuAction = mediaViewModel::menuAction,
					closeSelectionMode = { selectionModeClosed = it },
					navigate = navController::navigate
				)
			}
			composableI(AppRoute.PlaylistsScreen.route) {
				PlaylistsScreen(
					playlists = playlistViewModel.playlists,
					playlistAction = playlistViewModel::playlistActions,
					navigate = navController::navigate
				)
			}
			composableI(AppRoute.SettingsScreen.route) {
				SettingsScreen(
					uiState = settingsViewModel.settingsUiState,
					settingsAction = settingsViewModel::settingsAction,
					navigate = navController::navigate
				)
			}
			composableI(AppRoute.AboutUsScreen.route) { backStackEntry ->
				val screenToShow = backStackEntry.arguments?.getString("screenToShow") ?: ""

				AboutUsScreen(screenToShow, navController::navigate)
			}
			composableI(AppRoute.SearchScreen.route) {
				SearchScreen(
					uiState = searchViewModel.searchUiState,
					onSearch = searchViewModel::onSearch,
					menuAction = searchViewModel::menuAction,
					closeSelectionMode = { selectionModeClosed = it },
					navigate = navController::navigate
				)
			}
			composableI(
				route = AppRoute.ListScreen.route,
				arguments = listOf(
					navArgument("collectionName") { type = NavType.StringType },
					navArgument("collectionType") { type = NavType.StringType }
				)
			) { backStackEntry ->
				val collectionName = backStackEntry.arguments?.getString("collectionName") ?: ""
				val collectionType = backStackEntry.arguments?.getString("collectionType") ?: ""
				val previousScreenIsNowPlayingScreen = navController.previousBackStackEntry?.destination?.route == AppRoute.NowPlayingScreen.route
				listViewModel.getSongsFromCollection(collectionName, collectionType)

				ListScreen(
					uiState = listViewModel.listUiState,
					previousScreenIsNowPlayingScreen = previousScreenIsNowPlayingScreen,
					menuAction = mediaViewModel::menuAction,
					closeSelectionMode = { selectionModeClosed = it },
					navigate = navController::navigate
				)
			}
			composableI(AppRoute.AddSongsScreen.route) { backStackEntry ->
				val playlistName = backStackEntry.arguments?.getString("playlistToAddTo") ?: ""
				listViewModel.getSongsFromCollection(ADD_TO_PLAYLIST, playlistName)

				AddSongsScreen(
					uiState = listViewModel.listUiState,
					menuAction = mediaViewModel::menuAction,
					closeSelectionMode = {
						if (it) navController.navigateUp()
						selectionModeClosed = it
					}
				)
			}
			composableII(AppRoute.NowPlayingScreen.route) {
				NowPlayingScreen(
					uiState = nowPlayingViewModel.nowPlayingUiState,
					isDarkTheme = settingsViewModel.isDarkTheme ?: isSystemInDarkTheme(),
					mediaControl = nowPlayingViewModel::mediaControl,
					menuAction = mediaViewModel::menuAction,
					queueAction = nowPlayingViewModel::queueActions,
					navigate = navController::navigate
				)
			}
		}
	}
}