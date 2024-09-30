package com.roland.android.data_local.datastore

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roland.android.data_local.util.Constants.LIST_SEPARATOR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")
private val CURRENT_SONG_POSITION = intPreferencesKey("current_song_position")
private val CURRENT_SONG_SEEK_POSITION = longPreferencesKey("current_song_seek_position")
private val MUSIC_COLLECTION_TYPE = stringPreferencesKey("collection_type")
private val MUSIC_COLLECTION_NAME = stringPreferencesKey("collection_name")

class MusicQueueStore : KoinComponent {
	private val dataStore by inject<DataStore<Preferences>>()

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