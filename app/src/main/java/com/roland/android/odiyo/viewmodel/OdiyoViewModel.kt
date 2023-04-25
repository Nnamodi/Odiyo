package com.roland.android.odiyo.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.MediaSource
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.playingState
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
	}

	private fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSong = mediaItem?.localConfiguration?.uri
		return songs.find { it.uri == currentSong }
	}

	fun playAudio(uri: Uri) {
		mediaSession?.player?.apply {
			if (!isLoading) {
				val sameSong = currentMediaItem == uri.toMediaItem
				if (!sameSong) {
					while (!currentMediaItem?.equals(uri.toMediaItem)!!) {
						seekToNext()
						if (!hasNextMediaItem()) return
					}
					play()
				}
				if (isPlaying) {
					if (sameSong) pause()
				} else {
					if (sameSong) {
						prepare(); play()
					}
				}
			} else { return }
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
}