package com.roland.android.odiyo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

class NavActions(
	private val navController: NavHostController,
	private val storagePermissionGranted: Boolean,
	private val requestPermission: (Boolean) -> Unit
) {
	fun navigateToMediaScreen() {
		if (!storagePermissionGranted) { requestPermission(true); return }
		navController.navigate(AppRoute.MediaScreen.route)
	}
	fun navigateToPlaylistScreen() {
		if (!storagePermissionGranted) { requestPermission(true); return }
		navController.navigate(AppRoute.PlaylistsScreen.route)
	}
	fun navigateToNowPlayingScreen() {
		navController.navigate(AppRoute.NowPlayingScreen.route)
	}
	fun navigateToSearch() {
		navController.navigate(AppRoute.SearchScreen.route)
	}
	fun navigateToMediaItemScreen(collectionName: String, collectionType: String) {
		if (!storagePermissionGranted) { requestPermission(true); return }
		navController.navigate(
			AppRoute.MediaItemsScreen.routeWithName(collectionName, collectionType)
		)
	}
	fun navigateToAddSongsScreen(playlistToAddSongs: String) {
		if (!storagePermissionGranted) { requestPermission(true); return }
		navController.navigate(
			AppRoute.AddSongsScreen.routeWithName(playlistToAddSongs)
		)
	}
}

@Composable
fun concealMinimizedView(navController: NavHostController): Boolean {
	val navBackStackEntry = navController.currentBackStackEntryAsState()
	val currentDestination = navBackStackEntry.value?.destination?.route
	return currentDestination == AppRoute.NowPlayingScreen.route
}

sealed class AppRoute(val route: String) {
	object LibraryScreen: AppRoute("library_screen")
	object MediaScreen: AppRoute("media_screen")
	object PlaylistsScreen: AppRoute("playlists_screen")
	object NowPlayingScreen: AppRoute("now_playing_screen")
	object SearchScreen: AppRoute("search_screen")
	object MediaItemsScreen: AppRoute("media_item_screen/{collectionName}/{collectionType}") {
		fun routeWithName(collectionName: String, collectionType: String) =
			String.format("media_item_screen/%s/%s", collectionName, collectionType)
	}
	object AddSongsScreen: AppRoute("add_songs_screen/{playlistToAddTo}") {
		fun routeWithName(playlistToAddTo: String) = String.format("add_songs_screen/%s", playlistToAddTo)
	}
}

const val ALBUMS = "albums"
const val ARTISTS = "artists"
const val FAVORITES = "favorites"
const val LAST_PLAYED = "last_played"
const val PLAYLISTS = "playlists"