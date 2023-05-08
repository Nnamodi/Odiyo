package com.roland.android.odiyo.ui

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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.roland.android.odiyo.service.Util.convertToBitmap
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.viewmodel.MediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AppRoute(
	navController: NavHostController,
	viewModel: MediaViewModel
) {
	Scaffold(
		bottomBar = {
			BottomAppBar(
				concealBottomBar = currentRoute(navController = navController) != AppRoute.NowPlayingScreen.route,
				song = viewModel.currentSong,
				artwork = viewModel.nowPlayingMetaData?.convertToBitmap()
					?: viewModel.currentSong?.getArtwork(),
				isPlaying = viewModel.isPlaying,
				playPause = viewModel::playAudio,
				moveToNowPlayingScreen = { navController.navigate(AppRoute.NowPlayingScreen.route) }
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
					dataFromIntent = data,
					libraryTab = { LibraryTab(viewModel, navController) },
					albumsTab = { AlbumsTab(viewModel, navController) },
					artistsTab = { ArtistsTab(viewModel, navController) }
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
					"albums" -> viewModel.songsFromAlbum(collectionName)
					"artists" -> viewModel.songsFromArtist(collectionName)
					else -> emptyList()
				}

				MediaItemsScreen(
					songs = songs,
					collectionName = collectionName,
					currentSong = viewModel.currentSong,
					playAudio = { uri, index ->
						viewModel.mediaItems = songs.map { it.uri.toMediaItem }
						viewModel.playAudio(uri, index)
						index?.let { navController.navigate(AppRoute.NowPlayingScreen.route) }
					},
					navigateUp = { navController.navigateUp() }
				)
			}
			composable(AppRoute.NowPlayingScreen.route) {
				NowPlayingScreen(
					song = viewModel.currentSong,
					artwork = viewModel.nowPlayingMetaData?.convertToBitmap()
						?: viewModel.currentSong?.getArtwork(),
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
}

@Composable
fun currentRoute(navController: NavHostController): String? {
	val navBackStackEntry = navController.currentBackStackEntryAsState()
	return navBackStackEntry.value?.destination?.route
}

sealed class AppRoute(val route: String) {
	object MediaScreen: AppRoute("media_screen")
	object NowPlayingScreen: AppRoute("now_playing_screen")
	object MediaItemsScreen: AppRoute("media_item_screen/{collectionName}/{collectionType}") {
		fun routeWithName(collectionName: String, collectionType: String) =
			String.format("media_item_screen/%s/%s", collectionName, collectionType)
	}
}