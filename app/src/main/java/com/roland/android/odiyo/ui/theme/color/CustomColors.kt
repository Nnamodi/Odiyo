package com.roland.android.odiyo.ui.theme.color

import android.graphics.Bitmap
import android.graphics.Color.DKGRAY
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

object CustomColors {

	@Composable
	fun sliderColor(componentColor: Color): SliderColors {
		return SliderDefaults.colors(
			thumbColor = componentColor,
			activeTrackColor = componentColor,
			inactiveTrackColor = componentColor.copy(alpha = 0.3f)
		)
	}

	@Composable
	fun nowPlayingBackgroundColor(artwork: Bitmap?): Color {
		return if (artwork != null) {
			Color(dominantDarkColor(artwork))
		} else {
			MaterialTheme.colorScheme.outlineVariant
		}
	}

	fun componentColor(generatedColor: Color, toggled: Boolean = false): Color {
		val isDark = ColorUtils.calculateLuminance(generatedColor.hashCode()) < 0.1
		val componentColor = if (isDark) light_background else light_onBackground
		val toggleableComponentColor = if (isDark) dark_primary else light_primary
		return if (toggled) toggleableComponentColor else componentColor
	}

	private fun dominantDarkColor(image: Bitmap): Int {
		Palette.from(image).generate().let { palette: Palette ->
			val dominantColor = palette.getDominantColor(DKGRAY)
			val darkVibrantColor = palette.getDarkVibrantColor(palette.getDarkMutedColor(dominantColor))
			val dominantColorIsLight = ColorUtils.calculateLuminance(dominantColor.hashCode()) > 0.1
			val darkVibrantColorIsDarker = ColorUtils.calculateLuminance(darkVibrantColor.hashCode()) < 0.1
			return if (dominantColorIsLight && darkVibrantColorIsDarker) darkVibrantColor else dominantColor
		}
	}

	fun rippleColor(backgroundColor: Color): Color {
		val isDark = ColorUtils.calculateLuminance(backgroundColor.hashCode()) < 0.1
		return if (isDark) Color.White else Color.Black
	}
}