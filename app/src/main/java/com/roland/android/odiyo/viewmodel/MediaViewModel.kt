package com.roland.android.odiyo.viewmodel

import android.media.AudioManager
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
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.shuffleModeState
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
class MediaViewModel(
	private val repository: MediaRepository
) : ViewModel() {
	var songs by mutableStateOf<List<Music>>(emptyList())
	var mediaItems by mutableStateOf<List<MediaItem>>(emptyList())
	var albumList by mutableStateOf<List<Album>>(emptyList())
	var artistList by mutableStateOf<List<Artist>>(emptyList())

	var currentSong by mutableStateOf<Music?>(null)
	var isPlaying by mutableStateOf(false)
	var isDeviceMuted by mutableStateOf(false)
	var shuffleState by mutableStateOf(false)
	var nowPlayingMetaData by mutableStateOf<MediaMetadata?>(null)
	var progress by mutableStateOf(0f)

	private var initialDeviceVolume by mutableStateOf(0)

	init {
		viewModelScope.launch {
			repository.getAllSongs.collect { songList ->
				songs = songList
				mediaItems = songList.map { it.uri.toMediaItem }
				mediaSession?.player?.apply { setMediaItems(mediaItems); prepare() }
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
		viewModelScope.launch {
			repository.getAlbums.collect {
				albumList = it
			}
		}
		viewModelScope.launch {
			repository.getArtists.collect {
				artistList = it
			}
		}
	}

	private fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSong = mediaItem?.localConfiguration?.uri
		return songs.find { it.uri == currentSong }
	}

	private fun preparePlaylist() {
		mediaSession?.player?.apply {
			clearMediaItems()
			setMediaItems(mediaItems)
			prepare()
		}
	}

	fun playAudio(uri: Uri, index: Int? = null) {
		mediaSession?.player?.apply {
			if (isLoading) return
			// reset playlist when a mediaItem is selected from list
			index?.let {
				preparePlaylist()
				seekTo(it, 0)
			}
			val sameSong = currentMediaItem == uri.toMediaItem
			if (sameSong) {
				if (isPlaying) pause() else { prepare(); play() }
			}
			Log.d("ViewModelInfo", "playAudio: $index\n${musicItem(uri.toMediaItem)}")
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

	fun songsFromAlbum(albumName: String): List<Music> {
		var songsFromAlbum by mutableStateOf<List<Music>>(emptyList())
		viewModelScope.launch {
			repository.getSongsFromAlbum(
				arrayOf(albumName)
			).collect { songs ->
				songsFromAlbum = songs
			}
		}
		return songsFromAlbum
	}

	fun songsFromArtist(artistName: String): List<Music> {
		var songsFromArtist by mutableStateOf<List<Music>>(emptyList())
		viewModelScope.launch {
			repository.getSongsFromArtist(
				arrayOf(artistName)
			).collect { songs ->
				songsFromArtist = songs
			}
		}
		return songsFromArtist
	}
}