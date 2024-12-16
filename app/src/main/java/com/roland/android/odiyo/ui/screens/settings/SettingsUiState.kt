package com.roland.android.odiyo.ui.screens.settings

import androidx.annotation.StringRes
import com.roland.android.odiyo.R

data class SettingsUiState(
	@StringRes val theme: Int = R.string.follow_system,
	val shouldSaveSearchHistory: Boolean = true,
	val searchHistoryEmpty: Boolean = true,
	@StringRes val musicIntentOption: Int = R.string.always_ask
)