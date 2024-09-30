package com.roland.android.data_local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val SORT_PREFERENCE = stringPreferencesKey("sort_preference")
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