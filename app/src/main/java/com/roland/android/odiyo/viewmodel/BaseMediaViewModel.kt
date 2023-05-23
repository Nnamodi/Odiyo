package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.currentMediaIndex
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.songsOnQueue
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
open class BaseMediaViewModel(
	private val appDataStore: AppDataStore,
	private val repository: MediaRepository
) : ViewModel() {
	var songs by mutableStateOf<List<Music>>(emptyList()); private set
	var musicQueue by mutableStateOf<List<Music>>(emptyList())
	var currentMediaItemImage by mutableStateOf<Any?>(null); private set

	var currentSong by mutableStateOf<Music?>(null); private set
	var currentSongIndex by mutableStateOf(0); private set
	var isPlaying by mutableStateOf(false); private set
	private var nowPlayingMetaData by mutableStateOf<MediaMetadata?>(null)

	init {
		viewModelScope.launch {
			appDataStore.getCurrentPlaylist().collect {
				if (mediaItems.value.isEmpty()) {
					mediaItems.value = it.playlist.map { item -> item.toUri().toMediaItem }.toMutableList()
					mediaSession?.player?.apply {
						setMediaItems(mediaItems.value); prepare()
						if (mediaItems.value.isNotEmpty()) {
							seekTo(it.currentSongPosition, it.currentSongSeekPosition)
						}
					}
					Log.i("ViewModelInfo", "CurrentPlaylist: ${it.playlist.take(15)}, ${it.currentSongPosition}")
				}
			}
		}
		viewModelScope.launch {
			repository.getAllSongs.collect { songList ->
				songs = songList
			}
		}
		viewModelScope.launch {
			songsOnQueue.collect {
				musicQueue = it
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
			currentMediaIndex.collect {
				currentSongIndex = it
			}
		}
		viewModelScope.launch {
			nowPlayingMetadata.collect {
				nowPlayingMetaData = it
				currentMediaItemImage = it?.getArtwork()
				updateMusicQueue(queueEdited = false)
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
			setMediaItems(mediaItems.value)
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

	fun menuAction(context: Context, action: MediaMenuActions) {
		when (action) {
			is MediaMenuActions.PlayNext -> addToQueue(action.songs)
			is MediaMenuActions.RenameSong -> updateSong(action.details)
			is MediaMenuActions.ShareSong -> shareSong(context, action.details)
			is MediaMenuActions.DeleteSong -> deleteSong(action.details)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "menuAction: $action")
	}

	private fun addToQueue(song: List<Music>) {
		val mediaItems = song.map { it.uri.toMediaItem }
		mediaSession?.player?.apply {
			if (musicQueue.isNotEmpty()) {
				val index = currentMediaItemIndex + 1
				addMediaItems(index, mediaItems)
				Util.mediaItems.value.addAll(index, mediaItems)
			} else {
				pause()
				Util.mediaItems.value = mediaItems.toMutableList()
				preparePlaylist()
			}
		}
	}

	private fun updateSong(songDetails: SongDetails) {
		repository.updateSong(songDetails)
	}

	fun shareSong(context: Context, song: Music) {
		repository.shareSong(context, song)
	}

	private fun deleteSong(songDetails: SongDetails) {
		val songToDelete = songs.find { it.id == songDetails.id }
		repository.deleteSong(songDetails)
		if (musicQueue.contains(songToDelete)) {
			mediaItems.value.removeAll { it == songToDelete?.uri?.toMediaItem }
		}
	}

	private fun updateMusicQueue(queueEdited: Boolean = true) {
		songsOnQueue.value = try {
			mediaItems.value.map { musicItem(it)!! }.toMutableList()
		} catch (e: Exception) {
			Log.e("ViewModelInfo", "Couldn't fetch queue items", e)
			mutableListOf()
		}
		if (queueEdited || mediaItems.value.isNotEmpty()) saveCurrentPlaylist()
	}

	fun queueAction(action: QueueItemActions) {
		when (action) {
			is QueueItemActions.Play -> playFromQueue(action.item)
			is QueueItemActions.DuplicateSong -> duplicateSong(action.item)
			is QueueItemActions.RemoveSong -> removeSong(action.item)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "queueAction: $action")
	}

	private fun playFromQueue(song: QueueMediaItem) {
		mediaSession?.player?.apply {
			seekTo(song.index, 0)
			prepare(); play()
		}
	}

	private fun duplicateSong(song: QueueMediaItem) {
		val index = song.index + 1
		val mediaItem = song.uri.toMediaItem
		mediaSession?.player?.addMediaItem(index, mediaItem)
		mediaItems.value.add(index, mediaItem)
	}

	private fun removeSong(song: QueueMediaItem) {
		mediaSession?.player?.removeMediaItem(song.index)
		mediaItems.value.removeAt(song.index)
	}

	private fun saveCurrentPlaylist() {
		val player = mediaSession?.player
		val songPosition = player?.currentMediaItemIndex ?: 0
		val seekPosition = player?.currentPosition ?: 0
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveCurrentPlaylist(
				playlist = mediaItems.value.map { it.localConfiguration?.uri ?: "null".toUri() },
				currentPosition = songPosition,
				seekPosition = seekPosition
			)
		}
		Log.i("ViewModelInfo", "CurrentPlaylist saved")
	}
}