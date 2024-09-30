package com.roland.android.data_local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roland.android.domain.util.IntentOptions
import com.roland.android.domain.util.Themes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val THEMES = stringPreferencesKey("themes")
private val SAVE_SEARCH_HISTORY = booleanPreferencesKey("should_save_search_history")
private val MUSIC_INTENT = stringPreferencesKey("music_intent")

class SettingsStore : KoinComponent {
	private val dataStore by inject<DataStore<Preferences>>()

	suspend fun saveTheme(selectedTheme: Themes) {
		dataStore.edit { preference ->
			preference[THEMES] = selectedTheme.name
		}
	}

	fun getTheme(): Flow<Themes> {
		return dataStore.data.map { preference ->
			Themes.valueOf(
				value = preference[THEMES] ?: Themes.System.name
			)
		}
	}

	suspend fun setShouldSaveSearchHistory(shouldSave: Boolean) {
		dataStore.edit { preference ->
			preference[SAVE_SEARCH_HISTORY] = shouldSave
		}
	}

	fun getShouldSaveSearchHistory(): Flow<Boolean> {
		return dataStore.data.map { preference ->
			preference[SAVE_SEARCH_HISTORY] ?: true
		}
	}

	suspend fun saveMusicIntentOption(intentOption: IntentOptions) {
		dataStore.edit { preference ->
			preference[MUSIC_INTENT] = intentOption.name
		}
	}

	fun getMusicIntentOption(): Flow<IntentOptions> {
		return dataStore.data.map { preference ->
			IntentOptions.valueOf(
				value = preference[MUSIC_INTENT] ?: IntentOptions.AlwaysAsk.name
			)
		}
	}
}