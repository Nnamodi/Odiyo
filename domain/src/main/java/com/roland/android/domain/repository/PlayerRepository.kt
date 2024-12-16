package com.roland.android.domain.repository

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.ShuffleState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

	fun playSong(uri: Uri)

	fun playSong(
		uri: Uri,
		index: Int,
		songsToPlay: List<Music>,
		nowPlayingFrom: NowPlayingFrom
	)

	fun playPause(): Flow<Boolean>

	fun seek(previous: Boolean, next: Boolean)

	fun onSeekToPosition(position: Long)

	fun getRepeatMode(): Flow<Int>

	fun setRepeatMode(repeatMode: Int): Flow<Int>

	fun getShuffleState(): Flow<ShuffleState>

	fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState>

	fun onMuteDevice()

}