package com.roland.android.data_local.data_util

import com.roland.android.data_local.datastore.PlayerUtilStore
import com.roland.android.data_repository.data_util.local.LocalPlayerUtil
import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalPlayerUtilImpl : LocalPlayerUtil, KoinComponent {
	private val playerUtilStore by inject<PlayerUtilStore>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun setRepeatMode(repeatMode: Int): Flow<Int> {
		coroutineScope.launch {
			playerUtilStore.saveRepeatMode(repeatMode)
		}
		return playerUtilStore.getRepeatMode()
	}

	override fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState> {
		coroutineScope.launch {
			playerUtilStore.saveShuffleState(shouldShuffle, randomSeed)
		}
		return playerUtilStore.getShuffleState()
	}
}