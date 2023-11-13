package com.roland.android.odiyo.ui.theme.color

import android.graphics.Bitmap
import android.graphics.Color.DKGRAY
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.palette.graphics.Palette
import kotlinx.coroutines.launch

object CustomColors {
	private var currentColor = default_background

	@Composable
	fun sliderColor(componentColor: Color = MaterialTheme.colorScheme.background): SliderColors {
		return SliderDefaults.colors(
			thumbColor = componentColor,
			activeTrackColor = componentColor,
			inactiveTrackColor = componentColor.copy(alpha = 0.3f),
			disabledThumbColor = componentColor.copy(alpha = 0.4f),
			disabledActiveTrackColor = componentColor.copy(alpha = 0.4f),
			disabledInactiveTrackColor = componentColor.copy(alpha = 0.2f)
		)
	}

	@Composable
	fun nowPlayingBackgroundColor(artwork: Bitmap?): Color {
		val scope = rememberCoroutineScope()
		val newColor = remember { Animatable(currentColor) }
		val generatedColor = if (artwork != null) {
			Color(dominantDarkColor(artwork))
		} else MaterialTheme.colorScheme.outlineVariant

		LaunchedEffect(artwork) {
			scope.launch {
				newColor.animateTo(
					targetValue = generatedColor,
					animationSpec = tween(durationMillis = 2000, delayMillis = 500)
				)
				currentColor = newColor.value
			}
		}
		return newColor.value
	}

	private fun dominantDarkColor(image: Bitmap): Int {
		Palette.from(image).generate().let { palette: Palette ->
			val dominantColor = palette.getDominantColor(DKGRAY)
			val darkVibrantColor = palette.getDarkVibrantColor(palette.getDarkMutedColor(dominantColor))
			val dominantColorIsLight = Color(dominantColor).luminance() > 0.1
			val darkVibrantColorIsDarker = Color(darkVibrantColor).luminance() < 0.1
			return if (dominantColorIsLight && darkVibrantColorIsDarker) darkVibrantColor else dominantColor
		}
	}

	fun rippleColor(backgroundColor: Color): Color {
		val isDark = backgroundColor.luminance() < 0.1
		return if (isDark) Color.White else Color.Black
	}
}