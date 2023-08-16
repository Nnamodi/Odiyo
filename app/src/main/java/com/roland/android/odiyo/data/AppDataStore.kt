package com.roland.android.odiyo.data

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.media3.common.Player
import com.roland.android.odiyo.ui.dialog.SortOptions
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

	suspend fun saveSearchHistory(history: List<String>) {
		dataStore.edit { preference ->
			preference[SEARCH_HISTORY] = history.toSet().joinToString(LIST_SEPARATOR)
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
}