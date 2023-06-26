package com.roland.android.odiyo.data

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.roland.android.odiyo.ui.dialog.SortOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")
private val CURRENT_SONG_POSITION = intPreferencesKey("current_song_position")
private val CURRENT_SONG_SEEK_POSITION = longPreferencesKey("current_song_seek_position")
private val SORT_PREFERENCE = stringPreferencesKey("sort_preference")
private val SHUFFLE_STATE = booleanPreferencesKey("shuffle_state")

class AppDataStore(private val dataStore: DataStore<Preferences>) {
	suspend fun saveCurrentPlaylist(
		playlist: List<Uri>,
		currentPosition: Int,
		seekPosition: Long,
	) {
		dataStore.edit { preferences ->
			preferences[CURRENT_PLAYLIST] = playlist.joinToString(",")
			preferences[CURRENT_SONG_POSITION] = currentPosition
			preferences[CURRENT_SONG_SEEK_POSITION] = seekPosition
		}
	}

	fun getCurrentPlaylist(): Flow<CurrentPlaylist> {
		return dataStore.data.map { preferences ->
			CurrentPlaylist(
				playlist = preferences[CURRENT_PLAYLIST]?.split(",") ?: emptyList(),
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

	suspend fun saveShuffleState(shuffleState: Boolean) {
		dataStore.edit { preference ->
			preference[SHUFFLE_STATE] = shuffleState
		}
	}

	fun getShuffleState(): Flow<Boolean> {
		return dataStore.data.map { preference ->
			preference[SHUFFLE_STATE] ?: false
		}
	}
}