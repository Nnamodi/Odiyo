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
	fun nowPlayingBackgroundColor(artwork: Any?): Color {
		return if (artwork != null) {
			Color(dominantDarkColor(artwork as Bitmap))
		} else {
			MaterialTheme.colorScheme.outlineVariant
		}
	}

	fun componentColor(generatedColor: Color): Color {
		val isDark = ColorUtils.calculateLuminance(generatedColor.hashCode()) < 0.1
		return if (isDark) light_background else light_onBackground
	}

	private fun dominantDarkColor(image: Bitmap): Int {
		Palette.from(image).generate().let { palette: Palette ->
			return palette.getDarkVibrantColor(palette.getDarkMutedColor(DKGRAY))
		}
	}
}