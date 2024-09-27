package com.roland.android.domain.repository

import android.net.Uri
import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

	fun playSong(
		uri: Uri,
		index: Int,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun playPause(): Flow<Boolean>

	fun getCurrentStreamPosition(): Flow<Long>

	fun seek(previous: Boolean, next: Boolean)

	fun onSeekToPosition(position: Long)

	fun setRepeatMode(repeatMode: Int): Flow<Int>

	fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState>

	fun onMuteDevice(deviceVolume: Int)

}