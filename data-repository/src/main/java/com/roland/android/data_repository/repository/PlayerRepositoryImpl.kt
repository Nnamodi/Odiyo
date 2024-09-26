package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_util.system.PlayerUtil
import com.roland.android.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerRepositoryImpl : PlayerRepository, KoinComponent {
	private val playerUtil by inject<PlayerUtil>()

	override fun playSong(uri: Uri, index: Int, collectionType: String, collectionName: String) {
		playerUtil.playSong(uri, index, collectionType, collectionName)
	}

	override fun playPause(): Flow<Boolean> {
		return playerUtil.playPause()
	}

	override fun getCurrentStreamPosition(): Flow<Long> {
		return playerUtil.getCurrentStreamPosition()
	}

	override fun seek(previous: Boolean, next: Boolean) {
		playerUtil.seek(previous, next)
	}

	override fun onSeekToPosition(position: Long) {
		playerUtil.onSeekToPosition(position)
	}

	override fun setRepeatMode(repeatMode: Int): Flow<Int> {
		return playerUtil.setRepeatMode(repeatMode)
	}

	override fun onShuffle(shuffle: Boolean) {
		playerUtil.onShuffle(shuffle)
	}

	override fun onMuteDevice(deviceVolume: Int) {
		playerUtil.onMuteDevice(deviceVolume)
	}
}