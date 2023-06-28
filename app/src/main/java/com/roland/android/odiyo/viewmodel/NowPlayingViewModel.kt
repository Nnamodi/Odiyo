package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.progress
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.MediaControls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class NowPlayingViewModel @Inject constructor(
	private val appDataStore: AppDataStore,
	mediaRepository: MediaRepository,
	musicRepository: MusicRepository,
	playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {
	var isDeviceMuted by mutableStateOf(false); private set
	var shuffleState by mutableStateOf(false); private set
	var repeatMode by mutableStateOf(0); private set
	private var initialDeviceVolume by mutableStateOf(0)

	var seekProgress by mutableStateOf(0f); private set
	var currentDuration by mutableStateOf("00:00"); private set
	private var updateProgress = true

	init {
		viewModelScope.launch {
			playingState.collect {
				updateProgress()
			}
		}
		viewModelScope.launch {
			deviceMuteState.collect {
				isDeviceMuted = it
			}
		}
		viewModelScope.launch {
			appDataStore.getShuffleState().collect {
				shuffleState = it
				mediaSession?.player?.shuffleModeEnabled = it
			}
		}
		viewModelScope.launch {
			appDataStore.getRepeatMode().collect {
				repeatMode = it
				mediaSession?.player?.repeatMode = it
			}
		}
		viewModelScope.launch {
			progress.collect {
				currentDuration = it.time
				seekProgress = it.toFloat()
			}
		}
	}

	private fun updateProgress(): Boolean {
		return Handler(Looper.getMainLooper()).postDelayed({
			mediaSession?.player?.apply {
				seekProgress = currentPosition.toFloat()
				currentDuration = currentPosition.time
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
			when {
				previous -> seekToPrevious()
				next -> seekToNext()
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
			appDataStore.saveShuffleState(!shuffleState)
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