package com.roland.android.data_repository.data_util.player

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow

interface SystemPlayerUtil {

	fun playSong(
		uri: Uri,
		index: Int,
		songsToPlay: List<Music>
	)

	fun playPause(): Flow<Boolean>

	fun getCurrentSong(): Flow<Music?>

	fun seek(previous: Boolean, next: Boolean)

	fun onSeekToPosition(position: Long)

	fun setRepeatMode(repeatMode: Int): Flow<Int>

	fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState>

	fun onMuteDevice()

}