package com.roland.android.odiyo.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.MediaSource
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.shuffleModeState
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class OdiyoViewModel(
	resolver: ContentResolver
) : ViewModel() {
	private val mediaSource = MediaSource(viewModelScope, resolver)
	var songs by mutableStateOf<List<Music>>(emptyList())
	var mediaItems by mutableStateOf<List<MediaItem>>(emptyList())
	var currentSong by mutableStateOf<Music?>(null)
	var isPlaying by mutableStateOf(false)
	var isDeviceMuted by mutableStateOf(false)
	var shuffleState by mutableStateOf(false)
	var nowPlayingMetaData by mutableStateOf<MediaMetadata?>(null)
	var progress by mutableStateOf(0f)

	init {
		viewModelScope.launch {
			mediaSource.media().collect {
				songs = it
				mediaItems = it.map { music ->
					MediaItem.Builder().setUri(music.uri).build()
				}
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
			}
		}
		viewModelScope.launch {
			nowPlayingMetadata.collect {
				nowPlayingMetaData = it
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
	}

	private fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSong = mediaItem?.localConfiguration?.uri
		return songs.find { it.uri == currentSong }
	}

	fun playAudio(uri: Uri, index: Int? = null) {
		mediaSession?.player?.apply {
			if (!isLoading) {
				val sameSong = currentMediaItem == uri.toMediaItem
				// reset playlist when a mediaItem is selected from list
				index?.let {
					setMediaItems(mediaItems)
					seekTo(it, 0)
					prepare(); play()
				}
				if (isPlaying) {
					if (sameSong) pause()
				} else {
					if (sameSong) {
						prepare(); play()
					}
				}
			} else { return }
			Log.d("ViewModelInfo", "playAudio: ${musicItem(uri.toMediaItem)}")
		}
	}

	fun seek(previous: Boolean = false, next: Boolean = false) {
		mediaSession?.player?.apply {
			when {
				previous -> seekToPrevious()
				next -> seekToNext()
			}
		}
	}

	fun shuffle() {
		mediaSession?.player?.apply {
			shuffleModeEnabled = !shuffleModeEnabled
		}
	}

	fun onMuteDevice() {
		mediaSession?.player?.apply {
			isDeviceMuted = !isDeviceMuted
		}
	}
}