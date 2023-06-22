package com.roland.android.odiyo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

data class WindowSize(
	val width: WindowType,
	val height: WindowType
)

enum class WindowType { Portrait, Landscape }

@Composable
fun rememberWindowSize(): WindowSize {
	val configuration = LocalConfiguration.current
	val screenWidth by remember(configuration) {
		mutableStateOf(configuration.screenWidthDp)
	}
	val screenHeight by remember(configuration) {
		mutableStateOf(configuration.screenHeightDp)
	}

	return WindowSize(
		width = getScreenWidth(screenWidth),
		height = getScreenHeight(screenHeight)
	)
}

fun getScreenWidth(width: Int): WindowType = when {
	width < 600 -> WindowType.Portrait
	else -> WindowType.Landscape
}

fun getScreenHeight(height: Int): WindowType = when {
	height < 480 -> WindowType.Portrait
	else -> WindowType.Landscape
}

@Composable
fun sheetHeight(): Double {
	val windowSize = rememberWindowSize()
	val inLandscapeMode = windowSize.width == WindowType.Landscape || windowSize.height == WindowType.Portrait
	val inMultiWindowMode = inLandscapeMode && windowSize.height != WindowType.Portrait
	val divisor = if (inMultiWindowMode) 1.5 else 2.0
	return LocalConfiguration.current.screenHeightDp / divisor
}