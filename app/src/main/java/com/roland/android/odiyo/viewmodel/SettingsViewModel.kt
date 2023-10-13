package com.roland.android.odiyo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.service.Util.settingsUiState
import com.roland.android.odiyo.states.SettingsUiState
import com.roland.android.odiyo.ui.dialog.IntentOptions
import com.roland.android.odiyo.ui.dialog.Themes
import com.roland.android.odiyo.util.Haptic
import com.roland.android.odiyo.util.SettingsActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val appDataStore: AppDataStore,
	private val haptic: Haptic
) : ViewModel() {
	var isDarkTheme by mutableStateOf<Boolean?>(null)
	var musicIntentOption by mutableStateOf(IntentOptions.AlwaysAsk)

	var settingsScreenUiState by mutableStateOf(SettingsUiState()); private set

	init {
		viewModelScope.launch {
			appDataStore.getTheme().collectLatest { theme ->
				isDarkTheme = when (theme) {
					Themes.Light -> false
					Themes.Dark -> true
					else -> null
				}
				val selectedTheme = when (theme) {
					Themes.System -> R.string.system
					Themes.Dark -> R.string.dark_theme
					Themes.Light -> R.string.light_theme
				}
				settingsUiState.update { it.copy(theme = selectedTheme) }
			}
		}
		viewModelScope.launch {
			appDataStore.getShouldSaveSearchHistory().collectLatest { value ->
				settingsUiState.update { it.copy(shouldSaveSearchHistory = value) }
			}
		}
		viewModelScope.launch {
			appDataStore.getMusicIntent().collectLatest { intentOption ->
				musicIntentOption = intentOption
				val option = when (intentOption) {
					IntentOptions.Play -> R.string.play
					IntentOptions.PlayNext -> R.string.play_next
					IntentOptions.AddToQueue -> R.string.add_to_queue
					else -> R.string.always_ask
				}
				settingsUiState.update { it.copy(musicIntentOption = option) }
			}
		}
		viewModelScope.launch {
			settingsUiState.collectLatest {
				settingsScreenUiState = it
			}
		}
	}

	fun settingsAction(action: SettingsActions) {
		when (action) {
			is SettingsActions.SetTheme -> saveTheme(action.selectedTheme)
			SettingsActions.SaveSearchHistory -> shouldSaveSearchHistory()
			SettingsActions.ClearSearchHistory -> clearSearchHistory()
			is SettingsActions.SetIntentOption -> saveMusicIntent(action.intentOption)
		}
	}

	private fun saveTheme(selectedTheme: Themes) {
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveTheme(selectedTheme)
		}
	}

	private fun shouldSaveSearchHistory() {
		viewModelScope.launch(Dispatchers.IO) {
			val shouldSave = settingsScreenUiState.shouldSaveSearchHistory
			appDataStore.setShouldSaveSearchHistory(!shouldSave)
			haptic.vibrate()
		}
	}

	private fun clearSearchHistory() {
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveSearchHistory(history = null, clearHistory = true)
		}
	}

	private fun saveMusicIntent(intentOption: IntentOptions) {
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveMusicIntent(intentOption)
		}
	}
}