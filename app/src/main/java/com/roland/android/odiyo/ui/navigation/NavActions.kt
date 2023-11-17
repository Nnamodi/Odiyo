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
	fun navigateToSettingsScreen() {
		navController.navigate(AppRoute.SettingsScreen.route)
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
	fun navigateToAboutUsScreen(screenToShow: String) {
		navController.navigate(
			AppRoute.AboutUsScreen.routeToScreen(screenToShow)
		)
	}
}

@Composable
fun concealMinimizedView(navController: NavHostController): Boolean {
	val navBackStackEntry = navController.currentBackStackEntryAsState()
	val currentDestination = navBackStackEntry.value?.destination?.route ?: return true
	val certainDestinations = setOf(
		AppRoute.NowPlayingScreen.route,
		AppRoute.SettingsScreen.route,
		AppRoute.AboutUsScreen.route
	)
	return certainDestinations.any { currentDestination == it }
}

sealed class AppRoute(val route: String) {
	object LibraryScreen: AppRoute("library_screen")
	object MediaScreen: AppRoute("media_screen")
	object PlaylistsScreen: AppRoute("playlists_screen")
	object NowPlayingScreen: AppRoute("now_playing_screen")
	object SettingsScreen: AppRoute("settings_screen")
	object SearchScreen: AppRoute("search_screen")
	object MediaItemsScreen: AppRoute("media_item_screen/{collectionName}/{collectionType}") {
		fun routeWithName(collectionName: String, collectionType: String) =
			String.format("media_item_screen/%s/%s", collectionName, collectionType)
	}
	object AddSongsScreen: AppRoute("add_songs_screen/{playlistToAddTo}") {
		fun routeWithName(playlistToAddTo: String) = String.format("add_songs_screen/%s", playlistToAddTo)
	}
	object AboutUsScreen: AppRoute("about_us_screen/{screenToShow}") {
		fun routeToScreen(screenToShow: String) = String.format("about_us_screen/%s", screenToShow)
	}
}

// Collection types
const val ALBUMS = "albums"
const val ARTISTS = "artists"
const val FAVORITES = "favorites"
const val LAST_PLAYED = "last_played"
const val PLAYLISTS = "playlists"
const val SEARCH = "search"
const val RECENTLY_ADDED = "recently_added"
const val ALL_SONGS = "all_songs"
const val ADD_TO_PLAYLIST = "add_to_playlist"
const val ABOUT_US = "about_us"
const val SUPPORT = "support"