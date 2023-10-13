package com.roland.android.odiyo.util

import com.roland.android.odiyo.ui.dialog.IntentOptions
import com.roland.android.odiyo.ui.dialog.Themes

sealed interface SettingsActions {
	data class SetTheme(val selectedTheme: Themes): SettingsActions
	object SaveSearchHistory: SettingsActions
	object ClearSearchHistory: SettingsActions
	data class SetIntentOption(val intentOption: IntentOptions): SettingsActions
}