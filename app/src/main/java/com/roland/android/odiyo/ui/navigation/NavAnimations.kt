package com.roland.android.odiyo.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

private val LEFT = AnimatedContentTransitionScope.SlideDirection.Left
private val RIGHT = AnimatedContentTransitionScope.SlideDirection.Right
private val DOWN = AnimatedContentTransitionScope.SlideDirection.Down
private val UP = AnimatedContentTransitionScope.SlideDirection.Up
private const val DURATION_MILLIS = 700

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composableI(
	route: String,
	arguments: List<NamedNavArgument> = emptyList(),
	deepLinks: List<NavDeepLink> = emptyList(),
	content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
	route = route, arguments = arguments, deepLinks = deepLinks,
	enterTransition = { slideIntoContainer(LEFT, tween(DURATION_MILLIS)) },
	popExitTransition = { slideOutOfContainer(RIGHT, tween(DURATION_MILLIS)) },
	exitTransition = null, popEnterTransition = null, content = content
)

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composableII(
	route: String,
	arguments: List<NamedNavArgument> = emptyList(),
	deepLinks: List<NavDeepLink> = emptyList(),
	content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
	route = route, arguments = arguments, deepLinks = deepLinks,
	enterTransition = { slideIntoContainer(UP, tween(DURATION_MILLIS)) },
	popExitTransition = { slideOutOfContainer(DOWN, tween(DURATION_MILLIS)) },
	exitTransition = null, popEnterTransition = null, content = content
)