package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.local.LocalPlayerUtil
import com.roland.android.data_repository.data_util.system.SystemPlayerUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.ShuffleState
import com.roland.android.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerRepositoryImpl : PlayerRepository, KoinComponent {
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val localPlayerUtil by inject<LocalPlayerUtil>()
	private val systemPlayerUtil by inject<SystemPlayerUtil>()

	override fun playSong(
		uri: Uri,
		index: Int,
		songsToPlay: List<Music>,
		nowPlayingFrom: NowPlayingFrom
	) {
		systemPlayerUtil.playSong(uri, index, songsToPlay)
		localMusicUtil.saveCurrentPlaylistDetails(nowPlayingFrom)
	}

	override fun playPause(): Flow<Boolean> {
		return systemPlayerUtil.playPause()
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

	override fun onMuteDevice() {
		systemPlayerUtil.onMuteDevice()
	}
}