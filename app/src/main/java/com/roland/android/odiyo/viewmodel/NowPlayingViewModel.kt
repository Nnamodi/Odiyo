package com.roland.android.odiyo.viewmodel

import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.progress
import com.roland.android.odiyo.service.Util.shuffleModeState
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class NowPlayingViewModel(private val repository: MediaRepository) : ViewModel() {
	var songs by mutableStateOf<List<Music>>(emptyList()); private set
	var currentMediaItemImage by mutableStateOf<Any?>(null); private set

	var currentSong by mutableStateOf<Music?>(null); private set
	var isPlaying by mutableStateOf(false); private set
	var isDeviceMuted by mutableStateOf(false); private set
	var shuffleState by mutableStateOf(false); private set
	private var nowPlayingMetaData by mutableStateOf<MediaMetadata?>(null)

	var seekProgress by mutableStateOf(0f); private set
	var currentDuration by mutableStateOf("00:00"); private set
	private var updateProgress = true

	private var initialDeviceVolume by mutableStateOf(0)

	init {
		viewModelScope.launch {
			repository.getAllSongs.collect {
				songs = it
			}
		}
		viewModelScope.launch {
			nowPlaying.collect {
				currentSong = musicItem(it)
			}
		}
		viewModelScope.launch {
			playingState.collect {
				isPlaying = it
				updateProgress()
			}
		}
		viewModelScope.launch {
			nowPlayingMetadata.collect {
				nowPlayingMetaData = it
				currentMediaItemImage = it?.getArtwork()
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

	fun playAudio(uri: Uri) {
		mediaSession?.player?.apply {
			if (isLoading) return
			val sameSong = currentMediaItem == uri.toMediaItem
			if (sameSong) {
				if (isPlaying) pause() else { prepare(); play() }
			}
			Log.d("ViewModelInfo", "playAudio: ${musicItem(uri.toMediaItem)}")
		}
	}

	fun seek(previous: Boolean, next: Boolean) {
		mediaSession?.player?.apply {
			when {
				previous -> seekToPrevious()
				next -> seekToNext()
			}
		}
	}

	fun onSeekToPosition(position: Long) {
		mediaSession?.player?.seekTo(position)
	}

	fun shuffle() {
		mediaSession?.player?.apply {
			shuffleModeEnabled = !shuffleModeEnabled
		}
	}

	fun onMuteDevice(audioManager: AudioManager) {
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