package com.roland.android.odiyo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

class NavActions(private val navController: NavHostController) {
	fun navigateToNowPlayingScreen() {
		navController.navigate(AppRoute.NowPlayingScreen.route)
	}
	fun navigateToSearch() {
		navController.navigate(AppRoute.SearchScreen.route)
	}
	fun navigateToMediaItemScreen(collectionName: String, collectionType: String) {
		navController.navigate(AppRoute.MediaItemsScreen.routeWithName(collectionName, collectionType))
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
	object SearchScreen: AppRoute("search_screen")
	object MediaItemsScreen: AppRoute("media_item_screen/{collectionName}/{collectionType}") {
		fun routeWithName(collectionName: String, collectionType: String) =
			String.format("media_item_screen/%s/%s", collectionName, collectionType)
	}
}