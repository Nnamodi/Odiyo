package com.roland.android.data_repository.data_util.system

import android.net.Uri
import com.roland.android.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface SystemPlayerUtil {

	fun playSong(
		uri: Uri,
		index: Int,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun playPause(): Flow<Boolean>

	fun getCurrentSong(): Flow<Music>

	fun getCurrentStreamPosition(): Flow<Long>

	fun seek(previous: Boolean, next: Boolean)

	fun onSeekToPosition(position: Long)

	fun onMuteDevice(deviceVolume: Int)

}