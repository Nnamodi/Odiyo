package com.roland.android.odiyo.util

import com.roland.android.odiyo.ui.dialog.Themes

sealed interface SettingsActions {
	data class SetTheme(val selectedTheme: Themes): SettingsActions
}