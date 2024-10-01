package com.roland.android.data_local.data_util

import com.roland.android.data_local.database.SearchDao
import com.roland.android.data_local.datastore.SettingsStore
import com.roland.android.data_repository.data_util.local.LocalSettingsUtil
import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalSettingsUtilImpl : LocalSettingsUtil, KoinComponent {
	private val searchDao by inject<SearchDao>()
	private val settingsStore by inject<SettingsStore>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun getSelectedTheme(): Flow<Themes> {
		return settingsStore.getTheme()
	}

	override fun toggleTheme(selectedTheme: Themes) {
		coroutineScope.launch {
			settingsStore.saveTheme(selectedTheme)
		}
	}

	override fun getShouldSaveHistoryOption(): Flow<Boolean> {
		return settingsStore.getShouldSaveSearchHistory()
	}

	override fun toggleShouldSaveHistory(shouldSave: Boolean) {
		coroutineScope.launch {
			settingsStore.setShouldSaveSearchHistory(shouldSave)
		}
	}

	override fun clearSearchHistory() {
		coroutineScope.launch {
			searchDao.clearSearchHistory()
		}
	}

	override fun getMusicIntentOption(): Flow<IntentOptions> {
		return settingsStore.getMusicIntentOption()
	}

	override fun toggleMusicIntentOption(intentOption: IntentOptions) {
		coroutineScope.launch {
			settingsStore.saveMusicIntentOption(intentOption)
		}
	}

	override fun launchEmailApp(recipient: String) {
		// TODO()
	}
}