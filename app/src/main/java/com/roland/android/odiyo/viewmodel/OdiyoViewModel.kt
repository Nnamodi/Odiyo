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
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class OdiyoViewModel(
	resolver: ContentResolver
) : ViewModel() {
	private val mediaSource = MediaSource(viewModelScope, resolver)
	var songs by mutableStateOf<List<Music>>(emptyList())
	var mediaItems by mutableStateOf<List<MediaItem>>(emptyList())
	var nowPlaying = mediaSession?.player?.currentMediaItem

	init {
		viewModelScope.launch {
			mediaSource.media().collect {
				songs = it
				mediaItems = it.map { music ->
					MediaItem.Builder().setUri(music.uri).build()
				}
			}
		}
	}

	fun playAudio(uri: Uri) {
		val clickedItem = MediaItem.Builder().setUri(uri).build()
		mediaSession?.player?.apply {
			if (!isLoading) {
				val sameSong = currentMediaItem == clickedItem
				if (!sameSong) {
					while (!currentMediaItem?.equals(clickedItem)!!) {
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
				nowPlaying = clickedItem
			} else { return }
		}
	}
}