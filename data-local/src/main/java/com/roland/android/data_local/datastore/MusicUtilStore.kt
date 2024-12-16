package com.roland.android.data_local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val SORT_PREFERENCE = stringPreferencesKey("sort_preference")
private val MUSIC_COLLECTION_NAME = stringPreferencesKey("collection_name")
private val MUSIC_COLLECTION_TYPE = stringPreferencesKey("collection_type")
private val PERMISSION_STATUS = booleanPreferencesKey("permission_status")

class MusicUtilStore : KoinComponent {
	private val dataStore by inject<DataStore<Preferences>>()

	suspend fun saveSortPreference(sortOption: SortOptions) {
		dataStore.edit { preference ->
			preference[SORT_PREFERENCE] = sortOption.name
		}
	}

	fun getSortPreference(): Flow<SortOptions> {
		return dataStore.data.map { preference ->
			SortOptions.valueOf(
				value = preference[SORT_PREFERENCE] ?: SortOptions.NameAZ.name
			)
		}
	}

	suspend fun saveCurrentPlaylistDetails(details: NowPlayingFrom) {
		dataStore.edit { preference ->
			preference[MUSIC_COLLECTION_NAME] = details.collectionName
			preference[MUSIC_COLLECTION_TYPE] = details.collectionType
		}
	}

	fun getCurrentPlaylistDetails(): Flow<NowPlayingFrom> {
		return dataStore.data.map { preference ->
			NowPlayingFrom(
				collectionName = preference[MUSIC_COLLECTION_NAME] ?: "",
				collectionType = preference[MUSIC_COLLECTION_TYPE] ?: ""
			)
		}
	}

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
}