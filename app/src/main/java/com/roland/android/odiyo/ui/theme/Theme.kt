package com.roland.android.odiyo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.roland.android.odiyo.ui.theme.color.DarkColors
import com.roland.android.odiyo.ui.theme.color.LightColors

@Composable
fun OdiyoTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
	val colors = when {
		dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
		dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
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