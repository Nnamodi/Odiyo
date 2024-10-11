package com.roland.android.player.data_util

import android.net.Uri
import com.roland.android.data_repository.data_util.local.LocalPlayerUtil
import com.roland.android.data_repository.data_util.player.SystemPlayerUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.ShuffleState
import com.roland.android.player.player_utils.PlayerUtils
import com.roland.android.player.util.Converters.toMediaItems
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SystemPlayerUtilImpl : SystemPlayerUtil, KoinComponent {
	private val localPlayerUtil by inject<LocalPlayerUtil>()
	private val playerUtils by inject<PlayerUtils>()

	override fun playSong(
		uri: Uri,
		index: Int,
		songsToPlay: List<Music>
	) {
		playerUtils.playSong(
			uri = uri,
			index = index,
			mediaItems = songsToPlay.toMediaItems()
		)
	}

	override fun playPause(): Flow<Boolean> {
		return playerUtils.playPause()
	}

	override fun getCurrentSong(): Flow<Music?> {
		return playerUtils.getCurrentSong()
	}

	override fun seek(previous: Boolean, next: Boolean) {
		playerUtils.seek(previous, next)
	}

	override fun onSeekToPosition(position: Long) {
		playerUtils.onSeekToPosition(position)
	}

	override fun setRepeatMode(repeatMode: Int): Flow<Int> {
		playerUtils.setRepeatMode(repeatMode)
		return localPlayerUtil.setRepeatMode(repeatMode)
	}

	override fun onShuffle(shouldShuffle: Boolean, randomSeed: Int): Flow<ShuffleState> {
		playerUtils.setShuffleMode(shouldShuffle)
		return localPlayerUtil.onShuffle(shouldShuffle, randomSeed)
	}

	override fun onMuteDevice() {
		playerUtils.onMuteDevice()
	}
}