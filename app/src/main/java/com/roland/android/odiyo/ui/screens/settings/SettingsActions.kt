package com.roland.android.odiyo.ui.screens.settings

import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes

sealed interface SettingsActions {

	data class SetTheme(val selectedTheme: Themes): SettingsActions

	data object SaveSearchHistory: SettingsActions

	data object ClearSearchHistory: SettingsActions

	data class SetIntentOption(val intentOption: IntentOptions): SettingsActions

	data class ContactUs(val recipient: String): SettingsActions

}