package com.roland.android.domain.repository

import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

	fun getSelectedTheme(): Flow<Themes>

	fun toggleTheme(selectedTheme: Themes)

	fun getShouldSaveHistoryOption(): Flow<Boolean>

	fun toggleShouldSaveHistory(shouldSave: Boolean)

	fun clearSearchHistory()

	fun getMusicIntentOption(): Flow<IntentOptions>

	fun toggleMusicIntentOption(intentOption: IntentOptions)

	fun launchEmailApp(recipient: String)

}