package com.roland.android.odiyo.ui.theme.color

import android.graphics.Bitmap
import android.graphics.Color.DKGRAY
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

object CustomColors {

	@Composable
	fun sliderColor(): SliderColors {
		return SliderDefaults.colors(
			thumbColor = Color.White,
			activeTrackColor = Color.White,
			inactiveTrackColor = Color.White.copy(alpha = 0.3f)
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

	private fun dominantDarkColor(image: Bitmap): Int {
		Palette.from(image).generate().let { palette: Palette ->
			return palette.getDarkVibrantColor(palette.getDarkMutedColor(DKGRAY))
		}
	}
}