package com.roland.android.odiyo.data

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media3.common.Player
import com.roland.android.odiyo.ui.dialog.IntentOptions
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.dialog.Themes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val PERMISSION_STATUS = booleanPreferencesKey("permission_status")
private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")
private val CURRENT_SONG_POSITION = intPreferencesKey("current_song_position")
private val CURRENT_SONG_SEEK_POSITION = longPreferencesKey("current_song_seek_position")
private val SORT_PREFERENCE = stringPreferencesKey("sort_preference")
private val SHUFFLE_STATE = booleanPreferencesKey("shuffle_state")
private val SEARCH_HISTORY = stringPreferencesKey("search_history")
private val RANDOM_SEED = intPreferencesKey("random_seed")
private val REPEAT_MODE = intPreferencesKey("repeat_mode")
private val THEMES = stringPreferencesKey("themes")
private val SAVE_SEARCH_HISTORY = booleanPreferencesKey("should_save_search_history")
private val MUSIC_INTENT = stringPreferencesKey("music_intent")
private val MUSIC_COLLECTION_TYPE = stringPreferencesKey("collection_type")
private val MUSIC_COLLECTION_NAME = stringPreferencesKey("collection_name")

class AppDataStore(private val dataStore: DataStore<Preferences>) {
	suspend fun savePermissionStatus(permanentlyDenied: Boolean) {
		dataStore.edit { preference ->
			preference[PERMISSION_STATUS] = permanentlyDenied
		}
	}

	fun getPermissionStatus(): Flow<Boolean> {
		return dataStore.data.map { preference ->
			preference[PERMISSION_STATUS] ?: false
		}
	}

	suspend fun saveCurrentPlaylist(
		playlist: List<Uri>,
		currentPosition: Int,
		seekPosition: Long,
	) {
		dataStore.edit { preferences ->
			preferences[CURRENT_PLAYLIST] = playlist.joinToString(LIST_SEPARATOR)
			preferences[CURRENT_SONG_POSITION] = currentPosition
			preferences[CURRENT_SONG_SEEK_POSITION] = seekPosition
		}
	}

	fun getCurrentPlaylist(): Flow<CurrentPlaylist> {
		return dataStore.data.map { preferences ->
			CurrentPlaylist(
				playlist = preferences[CURRENT_PLAYLIST]?.split(LIST_SEPARATOR) ?: emptyList(),
				currentSongPosition = preferences[CURRENT_SONG_POSITION] ?: 0,
				currentSongSeekPosition = preferences[CURRENT_SONG_SEEK_POSITION] ?: 0
			)
		}
	}

	suspend fun saveSortPreference(sortOptions: SortOptions) {
		dataStore.edit { preference ->
			preference[SORT_PREFERENCE] = sortOptions.name
		}
	}

	fun getSortPreference(): Flow<SortOptions> {
		return dataStore.data.map { preference ->
			SortOptions.valueOf(
				value = preference[SORT_PREFERENCE] ?: SortOptions.NameAZ.name
			)
		}
	}

	suspend fun saveShuffleState(shuffleState: Boolean, randomSeed: Int) {
		dataStore.edit { preference ->
			preference[SHUFFLE_STATE] = shuffleState
			preference[RANDOM_SEED] = randomSeed
		}
	}

	fun getShuffleState(): Flow<ShuffleState> {
		return dataStore.data.map { preference ->
			ShuffleState(
				state = preference[SHUFFLE_STATE] ?: false,
				randomSeed = preference[RANDOM_SEED] ?: 5
			)
		}
	}

	fun getSearchHistory(): Flow<List<String>> {
		return dataStore.data.map { preference ->
			preference[SEARCH_HISTORY]?.split(LIST_SEPARATOR) ?: emptyList()
		}
	}

	suspend fun saveSearchHistory(
		history: List<String>?,
		clearHistory: Boolean = false
	) {
		dataStore.edit { preference ->
			val updatedHistory = (history ?: emptyList()).toSet().joinToString(LIST_SEPARATOR)
			preference[SEARCH_HISTORY] = if (clearHistory) "" else updatedHistory
		}
	}

	suspend fun saveRepeatMode(repeatMode: Int) {
		dataStore.edit { preference ->
			preference[REPEAT_MODE] = repeatMode
		}
	}

	fun getRepeatMode(): Flow<Int> {
		return dataStore.data.map { preference ->
			preference[REPEAT_MODE] ?: Player.REPEAT_MODE_OFF
		}
	}

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

	suspend fun saveMusicIntent(intentOption: IntentOptions) {
		dataStore.edit { preference ->
			preference[MUSIC_INTENT] = intentOption.name
		}
	}

	fun getMusicIntent(): Flow<IntentOptions> {
		return dataStore.data.map { preference ->
			IntentOptions.valueOf(
				value = preference[MUSIC_INTENT] ?: IntentOptions.AlwaysAsk.name
			)
		}
	}

	suspend fun saveCurrentPlaylistDetails(collectionType: String, collectionName: String) {
		dataStore.edit { preference ->
			preference[MUSIC_COLLECTION_TYPE] = collectionType
			preference[MUSIC_COLLECTION_NAME] = collectionName
		}
	}

	fun getCurrentPlaylistDetails(): Flow<NowPlayingFrom> {
		return dataStore.data.map { preference ->
			NowPlayingFrom(
				collectionType = preference[MUSIC_COLLECTION_TYPE] ?: "",
				collectionName = preference[MUSIC_COLLECTION_NAME] ?: ""
			)
		}
	}
}