package com.roland.android.data_local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val RANDOM_SEED = intPreferencesKey("random_seed")
private val REPEAT_MODE = intPreferencesKey("repeat_mode")
private val SHUFFLE_STATE = booleanPreferencesKey("shuffle_state")

class PlayerUtilStore : KoinComponent {
	private val dataStore by inject<DataStore<Preferences>>()

	suspend fun saveRepeatMode(repeatMode: Int) {
		dataStore.edit { preference ->
			preference[REPEAT_MODE] = repeatMode
		}
	}

	fun getRepeatMode(): Flow<Int> {
		return dataStore.data.map { preference ->
			preference[REPEAT_MODE] ?: 0 // REPEAT_MODE_OFF
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
}