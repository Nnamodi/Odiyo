package com.roland.android.data_repository.data_util.local

import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow

interface LocalPlayerUtil {

	fun saveCurrentPlaylistDetails(collectionType: String, collectionName: String)

	fun setRepeatMode(repeatMode: Int): Flow<Int>

	fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState>

}