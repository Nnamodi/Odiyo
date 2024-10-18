package com.roland.android.data_repository.data_util.local

import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow

interface LocalPlayerUtil {

	fun getRepeatMode(): Flow<Int>

	fun setRepeatMode(repeatMode: Int): Flow<Int>

	fun getShuffleState(): Flow<ShuffleState>

	fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState>

}