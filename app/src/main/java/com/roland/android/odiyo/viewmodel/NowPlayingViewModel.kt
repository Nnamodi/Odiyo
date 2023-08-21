package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlayingUiState
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.MediaControls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(UnstableApi::class)
class NowPlayingViewModel @Inject constructor(
	private val appDataStore: AppDataStore,
	exoPlayer: ExoPlayer,
	mediaRepository: MediaRepository,
	musicRepository: MusicRepository,
	playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {
	private var shuffleState by mutableStateOf(false)
	private var repeatMode by mutableStateOf(0)
	private var initialDeviceVolume by mutableStateOf(0)

	private var updateProgress = true

	init {
		viewModelScope.launch {
			playingState.collect { updateProgress() }
		}
		viewModelScope.launch {
			appDataStore.getShuffleState().collect { shuffle ->
				shuffleState = shuffle.state
				nowPlayingUiState.update { it.copy(shuffleState = shuffle.state) }
				val shuffleOrder = DefaultShuffleOrder(mediaItems.value.size, shuffle.randomSeed.toLong())
				exoPlayer.setShuffleOrder(shuffleOrder)
				mediaSession?.player?.shuffleModeEnabled = shuffle.state
			}
		}
		viewModelScope.launch {
			appDataStore.getRepeatMode().collect { mode ->
				repeatMode = mode
				nowPlayingUiState.update { it.copy(repeatMode = mode) }
				mediaSession?.player?.repeatMode = mode
			}
		}
	}

	private fun updateProgress(): Boolean {
		return Handler(Looper.getMainLooper()).postDelayed({
			mediaSession?.player?.apply {
				nowPlayingUiState.update {
					it.copy(currentDuration = currentPosition.time, seekProgress = currentPosition.toFloat())
				}
			}
			if (updateProgress) updateProgress()
		}, 100L)
	}

	fun mediaControl(context: Context, action: MediaControls) {
		if (!canAccessStorage) return
		when (action) {
			MediaControls.Mute -> onMuteDevice(context)
			is MediaControls.PlayPause -> playPause()
			is MediaControls.Favorite -> favoriteSong(action.song)
			MediaControls.RepeatMode -> setRepeatMode()
			is MediaControls.Seek -> seek(action.previous, action.next)
			is MediaControls.SeekToPosition -> onSeekToPosition(action.position)
			is MediaControls.Share -> shareSong(context, listOf(action.music))
			MediaControls.Shuffle -> shuffle()
		}
		updateMusicQueue(queueEdited = false)
	}

	private fun playPause() {
		mediaSession?.player?.apply {
			if (isLoading) return
			if (isPlaying) pause() else { prepare(); play() }
			Log.d("ViewModelInfo", "playAudio: ${musicItem(currentMediaItem?.localConfiguration?.uri?.toMediaItem)}")
		}
	}

	private fun seek(previous: Boolean, next: Boolean) {
		mediaSession?.player?.apply {
			val songHasStarted = currentPosition >= 5000
			when {
				previous -> if (songHasStarted) seekToPrevious() else seekToPreviousMediaItem()
				next -> seekToNextMediaItem()
			}
		}
	}

	private fun onSeekToPosition(position: Long) {
		mediaSession?.player?.seekTo(position)
	}

	private fun setRepeatMode() {
		val mode = when (repeatMode) {
			Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
			Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
			else -> Player.REPEAT_MODE_OFF
		}
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveRepeatMode(mode)
		}
	}

	private fun shuffle() {
		viewModelScope.launch(Dispatchers.IO) {
			val randomSeed = (0..mediaItems.value.size).random()
			appDataStore.saveShuffleState(!shuffleState, randomSeed)
		}
	}

	private fun onMuteDevice(context: Context) {
		val audioManager = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
		val streamType = AudioManager.STREAM_MUSIC
		val setVolume: (Int) -> Unit = { audioManager.setStreamVolume(streamType, it, 0) }

		if (audioManager.isStreamMute(streamType)) {
			if (initialDeviceVolume == 0) initialDeviceVolume++
			setVolume(initialDeviceVolume)
		} else {
			initialDeviceVolume = audioManager.getStreamVolume(streamType)
			setVolume(0)
		}
	}

	override fun onCleared() {
		super.onCleared()
		updateProgress = false
	}
}