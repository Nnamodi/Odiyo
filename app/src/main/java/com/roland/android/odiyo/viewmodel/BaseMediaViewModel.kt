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
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.currentMediaIndex
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.songsOnQueue
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
open class BaseMediaViewModel(
	private val appDataStore: AppDataStore,
	private val mediaRepository: MediaRepository,
	private val musicRepository: MusicRepository,
	private val playlistRepository: PlaylistRepository
) : ViewModel() {
	var songs by mutableStateOf<List<Music>>(emptyList()); private set
	var lastPlayedSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var favoriteSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var recentSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var musicQueue by mutableStateOf<List<Music>>(emptyList()); private set
	private var songsFetched by mutableStateOf(false)

	var playlists by mutableStateOf<List<Playlist>>(emptyList()); private set

	var sortOrder by mutableStateOf(SortOptions.NameAZ)

	var currentMediaItemImage by mutableStateOf<Any?>(null)
	var currentSong by mutableStateOf<Music?>(null); private set
	var currentSongIndex by mutableStateOf(0); private set
	var isPlaying by mutableStateOf(false); private set

	init {
		viewModelScope.launch {
			playlistRepository.getPlaylists.collectLatest {
				playlists = it
			}
		}
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
			musicRepository.getCachedSongs.collectLatest { musicList ->
				songs = musicList
					.filter { it.name.endsWith(".mp3") }
					.sortList()
				lastPlayedSongs = songs
					.filter { it.lastPlayed != Date(0) }
					.sortedByDescending { it.lastPlayed }
					.take(100)
				favoriteSongs = songs.filter { it.favorite }
				recentSongs = songs
					.sortedByDescending { it.addedOn }
					.take(45)
				Log.i("DataInfo", "Cached songs: ${songs.size} | Songs fetched: $songsFetched")
				if (!songsFetched) fetchSongs()
			}
		}
		viewModelScope.launch {
			songsOnQueue.collect {
				musicQueue = it
			}
		}
		viewModelScope.launch {
			nowPlaying.collect { item ->
				currentSong = musicItem(item)
				currentSong?.let { saveStreamDate(it) }
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
				if (!songsFetched) return@collect
				if (currentSong !in songs) currentSong = musicItem(it)
				updateMusicQueue(queueEdited = false)
			}
		}
		viewModelScope.launch {
			appDataStore.getSortPreference().collectLatest {
				sortOrder = it
				songs = songs.sortList()
			}
		}
	}

	private fun fetchSongs() {
		if (songsFetched) return
		Log.i("DataInfo", "Call to fetch songs")
		viewModelScope.launch(Dispatchers.IO) {
			songsFetched = true
			mediaRepository.getSongsFromSystem.collect { musicFromSystems ->
				val musicList = musicFromSystems.map {
					Music(it.id, it.uri, it.name, it.title, it.artist, it.time, it.bytes, it.addedOn, it.album, it.path)
				}
				val allSongs = musicRepository.getCachedSongs(musicList, songs)
				musicRepository.apply {
					clear(); cacheSongs(allSongs)
				}
				Log.i("DataInfo", "All songs: ${allSongs.size} | Songs fetched: $songsFetched")
			}
		}
	}

	fun List<Music>.sortList(): List<Music> {
		return when (sortOrder) {
			SortOptions.NameAZ -> sortedBy { it.title }
			SortOptions.NameZA -> sortedByDescending { it.title }
			SortOptions.NewestFirst -> sortedByDescending { it.addedOn }
			SortOptions.OldestFirst -> sortedBy { it.addedOn }
		}
	}

	private fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSongUri = mediaItem?.localConfiguration?.uri
		return songs.find { it.uri == currentSongUri }
	}

	private fun musicItem(metadata: MediaMetadata?): Music {
		val time = mediaSession?.player?.duration ?: 0
		return Music(
			id = 0, uri = "".toUri(), name = "",
			title = (metadata?.title ?: "Unknown") as String,
			artist = (metadata?.artist ?: "Unknown") as String,
			time = if (time < 0) 0 else time, bytes = 0,
			addedOn = 0, album = "Unknown", path = "Unknown"
		)
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
			if (isPlaying) pause() else play()
			Log.d("ViewModelInfo", "playAudio: $index\n${musicItem(uri.toMediaItem)}")
		}
	}

	fun menuAction(context: Context, action: MediaMenuActions) {
		when (action) {
			is MediaMenuActions.PlayNext -> playNext(action.songs)
			is MediaMenuActions.AddToQueue -> addToQueue(action.songs)
			is MediaMenuActions.RenameSong -> renameSong(action.details)
			is MediaMenuActions.Favorite -> favoriteSong(action.song)
			is MediaMenuActions.ShareSong -> shareSong(context, action.details)
			is MediaMenuActions.SortSongs -> sortSongs(action.sortOptions)
			is MediaMenuActions.DeleteSong -> deleteSong(action.details)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "menuAction: $action")
	}

	private fun playNext(song: List<Music>) {
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

	private fun addToQueue(song: List<Music>) {
		val mediaItems = song.map { it.uri.toMediaItem }
		mediaSession?.player?.apply {
			if (musicQueue.isNotEmpty()) {
				addMediaItems(mediaItems)
				Util.mediaItems.value.addAll(mediaItems)
			} else {
				pause()
				Util.mediaItems.value = mediaItems.toMutableList()
				preparePlaylist()
			}
		}
	}

	private fun renameSong(songDetails: SongDetails) {
		mediaRepository.updateSongInSystem(songDetails)
		viewModelScope.launch(Dispatchers.IO) {
			val song = songs.find { it.id == songDetails.id }
			song?.let {
				songDetails.title?.let { song.title = it }
				songDetails.artist?.let { song.artist = it }
				musicRepository.updateSongInCache(song)
			}
		}
	}

	fun favoriteSong(song: Music) {
		viewModelScope.launch(Dispatchers.IO) {
			song.apply { favorite = !favorite }
			musicRepository.updateSongInCache(song)
		}
	}

	private fun saveStreamDate(song: Music) {
		viewModelScope.launch(Dispatchers.IO) {
			song.lastPlayed = Calendar.getInstance().time
			musicRepository.updateSongInCache(song)
		}
	}

	fun shareSong(context: Context, song: Music) {
		mediaRepository.shareSong(context, song)
	}

	private fun sortSongs(sortOption: SortOptions) {
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveSortPreference(sortOption)
		}
	}

	private fun deleteSong(songDetails: SongDetails) {
		val songToDelete = songs.find { it.id == songDetails.id }
		mediaRepository.deleteSongFromSystem(songDetails)
		if (musicQueue.contains(songToDelete)) {
			mediaItems.value.removeAll { it == songToDelete?.uri?.toMediaItem }
		}
		viewModelScope.launch(Dispatchers.IO) {
			val song = songs.find { it.id == songDetails.id }
			song?.let { musicRepository.deleteSongFromCache(song) }
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