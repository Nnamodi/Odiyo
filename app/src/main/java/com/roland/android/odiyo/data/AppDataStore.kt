package com.roland.android.odiyo.data

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")
private val CURRENT_SONG_POSITION = intPreferencesKey("current_song_position")
private val CURRENT_SONG_SEEK_POSITION = longPreferencesKey("current_song_seek_position")

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
}