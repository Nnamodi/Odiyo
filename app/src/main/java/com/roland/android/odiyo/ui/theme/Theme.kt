package com.roland.android.odiyo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.roland.android.odiyo.ui.theme.color.DarkColors
import com.roland.android.odiyo.ui.theme.color.LightColors

@Composable
fun OdiyoTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
	val colors = when {
		// Suspend dynamic theming because of custom dynamic theme in app.
//		dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
//		dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
		darkTheme -> DarkColors
		else -> LightColors
	}

	MaterialTheme(
		colorScheme = colors,
		shapes = Shapes,
		typography = Typography,
		content = content
	)
}