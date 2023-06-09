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
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.progress
import com.roland.android.odiyo.service.Util.shuffleModeState
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.MediaControls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class NowPlayingViewModel @Inject constructor(
	appDataStore: AppDataStore,
	mediaRepository: MediaRepository,
	musicRepository: MusicRepository,
	playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {
	var isDeviceMuted by mutableStateOf(false); private set
	var shuffleState by mutableStateOf(false); private set
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
			shuffleModeState.collect {
				shuffleState = it
			}
		}
		viewModelScope.launch {
			progress.collect {
				currentDuration = it.time
				seekProgress = it.toFloat()
			}
		}
	}

	private fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSong = mediaItem?.localConfiguration?.uri
		return songs.find { it.uri == currentSong }
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
		when (action) {
			MediaControls.Mute -> onMuteDevice(context)
			is MediaControls.PlayPause -> playPause()
			is MediaControls.Favorite -> favoriteSong(action.song)
			is MediaControls.Seek -> seek(action.previous, action.next)
			is MediaControls.SeekToPosition -> onSeekToPosition(action.position)
			is MediaControls.Share -> shareSong(context, action.music)
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

	private fun shuffle() {
		mediaSession?.player?.apply {
			shuffleModeEnabled = !shuffleModeEnabled
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