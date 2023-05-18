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
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MediaViewModel(
	private val appDataStore: AppDataStore,
	private val repository: MediaRepository
) : ViewModel() {
	var songs by mutableStateOf<List<Music>>(emptyList()); private set
	var mediaItems by mutableStateOf<List<MediaItem>>(emptyList())
	var albumList by mutableStateOf<List<Album>>(emptyList()); private set
	var artistList by mutableStateOf<List<Artist>>(emptyList()); private set
	var currentMediaItemImage by mutableStateOf<Any?>(null); private set

	var currentSong by mutableStateOf<Music?>(null); private set
	var isPlaying by mutableStateOf(false); private set
	private var nowPlayingMetaData by mutableStateOf<MediaMetadata?>(null)
	var searchQuery by mutableStateOf("")

	init {
		viewModelScope.launch {
			appDataStore.getCurrentPlaylist().collect {
				if (mediaItems.isEmpty()) {
					mediaItems = it.playlist.map { item -> item.toUri().toMediaItem }
					mediaSession?.player?.apply {
						setMediaItems(mediaItems); prepare()
						if (mediaItems.isNotEmpty()) seekTo(it.currentSongPosition, it.currentSongSeekPosition)
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
				currentMediaItemImage = it?.getArtwork()
				if (mediaItems.isNotEmpty()) saveCurrentPlaylist()
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

	fun songsFromAlbum(albumName: String): List<Music> {
		var songsFromAlbum by mutableStateOf<List<Music>>(emptyList())
		viewModelScope.launch {
			repository.getSongsFromAlbum(
				arrayOf(albumName)
			).collect { songs ->
				songsFromAlbum = songs.map {
					it.copy(thumbnail = it.getArtwork())
				}
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
				songsFromArtist = songs.map {
					it.copy(thumbnail = it.getArtwork())
				}
			}
		}
		return songsFromArtist
	}

	fun songsFromSearch(): List<Music> {
		val result = songs.filter { music ->
			val matchingCombinations = listOf(
				"${music.artist}${music.title}",
				"${music.artist} ${music.title}",
				"${music.title}${music.artist}",
				"${music.title} ${music.artist}",
				music.title, music.artist
			)
			matchingCombinations.any { it.contains(searchQuery, ignoreCase = true) }
		}
		return result
	}

	fun menuAction(context: Context, action: MediaMenuActions) {
		when (action) {
			is MediaMenuActions.PlayNext -> addToQueue(action.song)
			is MediaMenuActions.RenameSong -> updateSong(action.details)
			is MediaMenuActions.ShareSong -> shareSong(context, action.details)
			is MediaMenuActions.DeleteSong -> deleteSong(action.details)
		}
		Log.d("ViewModelInfo", "menuAction: $action")
	}

	private fun addToQueue(song: Music) {
		val mediaItem = song.uri.toMediaItem
		mediaSession?.player?.apply {
			val index = nextMediaItemIndex
			addMediaItem(index, mediaItem)
		}
	}

	private fun updateSong(songDetails: SongDetails) {
		repository.updateSong(songDetails)
	}

	private fun shareSong(context: Context, song: Music) {
		repository.shareSong(context, song)
	}

	private fun deleteSong(songDetails: SongDetails) {
		repository.deleteSong(songDetails)
	}

	private fun saveCurrentPlaylist() {
		val player = mediaSession?.player
		val songPosition = player?.currentMediaItemIndex ?: 0
		val seekPosition = player?.currentPosition ?: 0
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveCurrentPlaylist(
				playlist = mediaItems.map { it.localConfiguration?.uri ?: "null".toUri() },
				currentPosition = songPosition,
				seekPosition = seekPosition
			)
		}
	}
}