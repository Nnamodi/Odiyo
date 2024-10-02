package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_util.local.LocalPlayerUtil
import com.roland.android.data_repository.data_util.system.SystemPlayerUtil
import com.roland.android.domain.model.ShuffleState
import com.roland.android.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerRepositoryImpl : PlayerRepository, KoinComponent {
	private val localPlayerUtil by inject<LocalPlayerUtil>()
	private val systemPlayerUtil by inject<SystemPlayerUtil>()

	override fun playSong(uri: Uri, index: Int, collectionType: String, collectionName: String) {
		systemPlayerUtil.playSong(uri, index)
		localPlayerUtil.saveCurrentPlaylistDetails(collectionType, collectionName)
	}

	override fun playPause(): Flow<Boolean> {
		return systemPlayerUtil.playPause()
	}

	override fun getCurrentStreamPosition(): Flow<Long> {
		return systemPlayerUtil.getCurrentStreamPosition()
	}

	override fun seek(previous: Boolean, next: Boolean) {
		systemPlayerUtil.seek(previous, next)
	}

	override fun onSeekToPosition(position: Long) {
		systemPlayerUtil.onSeekToPosition(position)
	}

	override fun setRepeatMode(repeatMode: Int): Flow<Int> {
		return localPlayerUtil.setRepeatMode(repeatMode)
	}

	override fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState> {
		return localPlayerUtil.onShuffle(shouldShuffle, randomSeed)
	}

	override fun onMuteDevice(deviceVolume: Int) {
		systemPlayerUtil.onMuteDevice(deviceVolume)
	}
}