package com.roland.android.data_repository.repository

import com.roland.android.data_repository.data_util.local.LocalSettingsUtil
import com.roland.android.domain.repository.SettingsRepository
import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsRepositoryImpl : SettingsRepository, KoinComponent {
	private val localSettingUtil by inject<LocalSettingsUtil>()

	override fun getSelectedTheme(): Flow<Themes> {
		return localSettingUtil.getSelectedTheme()
	}

	override fun toggleTheme(selectedTheme: Themes) {
		localSettingUtil.toggleTheme(selectedTheme)
	}

	override fun getShouldSaveHistoryOption(): Flow<Boolean> {
		return localSettingUtil.getShouldSaveHistoryOption()
	}

	override fun toggleShouldSaveHistory(shouldSave: Boolean) {
		localSettingUtil.toggleShouldSaveHistory(shouldSave)
	}

	override fun clearSearchHistory() {
		localSettingUtil.clearSearchHistory()
	}

	override fun getMusicIntentOption(): Flow<IntentOptions> {
		return localSettingUtil.getMusicIntentOption()
	}

	override fun toggleMusicIntentOption(intentOption: IntentOptions) {
		localSettingUtil.toggleMusicIntentOption(intentOption)
	}

	override fun launchEmailApp(recipient: String) {
		localSettingUtil.launchEmailApp(recipient)
	}
}