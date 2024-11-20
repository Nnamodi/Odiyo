package com.roland.android.odiyo.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.domain.repository.SettingsRepository
import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes
import com.roland.android.odiyo.util.Haptic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {
	private val haptic by inject<Haptic>()
	private val settingsRepository by inject<SettingsRepository>()

	var isDarkTheme by mutableStateOf<Boolean?>(null); private set
	var musicIntentOption by mutableStateOf(IntentOptions.AlwaysAsk); private set

	private var _settingsUiState = MutableStateFlow(SettingsUiState())
	var settingsUiState by mutableStateOf(_settingsUiState.value); private set

	init {
		viewModelScope.launch {
			combine(
				settingsRepository.getSelectedTheme(),
				settingsRepository.getShouldSaveHistoryOption(),
				settingsRepository.getMusicIntentOption(),
			) { theme, shouldSaveSearch, musicIntentOption ->
				isDarkTheme = theme.isDark()
				_settingsUiState.update {
					it.copy(
						theme = theme.title,
						shouldSaveSearchHistory = shouldSaveSearch,
						musicIntentOption = musicIntentOption.menuText
					)
				}
			}
		}
		viewModelScope.launch {
			_settingsUiState.collectLatest {
				settingsUiState = it
			}
		}
	}

	fun settingsAction(action: SettingsActions) {
		when (action) {
			is SettingsActions.SetTheme -> saveTheme(action.selectedTheme)
			SettingsActions.SaveSearchHistory -> shouldSaveSearchHistory()
			SettingsActions.ClearSearchHistory -> clearSearchHistory()
			is SettingsActions.SetIntentOption -> saveMusicIntent(action.intentOption)
			is SettingsActions.ContactUs -> launchEmailApp(action.recipient)
		}
	}

	private fun saveTheme(selectedTheme: Themes) {
		settingsRepository.toggleTheme(selectedTheme)
	}

	private fun shouldSaveSearchHistory() {
		val shouldSave = settingsUiState.shouldSaveSearchHistory
		settingsRepository.toggleShouldSaveHistory(!shouldSave)
		haptic.vibrate()
	}

	private fun clearSearchHistory() {
		settingsRepository.clearSearchHistory()
	}

	private fun saveMusicIntent(intentOption: IntentOptions) {
		settingsRepository.toggleMusicIntentOption(intentOption)
	}

	private fun launchEmailApp(recipient: String) {
		settingsRepository.launchEmailApp(recipient)
	}

	private fun Themes.isDark() = when (this) {
		Themes.Light -> false
		Themes.Dark -> true
		else -> null
	}
}