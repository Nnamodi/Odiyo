package com.roland.android.odiyo.viewmodel

import android.content.Context
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
import com.roland.android.odiyo.service.Util.storagePermissionGranted
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
	private var cachedSongs by mutableStateOf<List<Music>>(emptyList())
	var songs by mutableStateOf<List<Music>>(emptyList())
	var albumList by mutableStateOf<List<Album>>(emptyList()); private set
	var artistList by mutableStateOf<List<Artist>>(emptyList()); private set

	var lastPlayedSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var favoriteSongs by mutableStateOf<List<Music>>(emptyList())
	var recentSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var musicQueue by mutableStateOf<List<Music>>(emptyList()); private set
	private var songsFetched by mutableStateOf(false)
	var sortOrder by mutableStateOf(SortOptions.NameAZ)

	var canAccessStorage by mutableStateOf(false)

	var playlists by mutableStateOf<List<Playlist>>(emptyList()); private set
	var songsFromPlaylist by mutableStateOf<List<Music>>(emptyList()); private set

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
			songsOnQueue.collect {
				musicQueue = it
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
			storagePermissionGranted.collectLatest {
				canAccessStorage = it
				if (!it) return@collectLatest
				getAllSongs(); restoreCurrentPlaylist()
				getCurrentSong(); getNowPlayingMetadata()
				getAlbums(); getArtists()
			}
		}
	}

	private fun getAllSongs() {
		viewModelScope.launch {
			musicRepository.getCachedSongs.collectLatest { musicList ->
				cachedSongs = musicList
				if (!songsFetched) {
					fetchAndSyncSongs()
					return@collectLatest
				}
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
			}
		}
	}

	private fun fetchAndSyncSongs() {
		if (songsFetched) return
		Log.i("DataInfo", "Call to fetch songs")
		viewModelScope.launch {
			songsFetched = true
			mediaRepository.getSongsFromSystem().collect { musicFromSystems ->
				val musicList = musicFromSystems.map {
					Music(it.id, it.uri, it.name, it.title, it.artist, it.time, it.bytes, it.addedOn, it.album, it.path)
				}
				// return cachedSongs to default sort order before sending up for processing to avoid duplicate songs in the database.
				cachedSongs.filter { it.name.endsWith(".mp3") }.sortedBy { it.title }

				val allSongs = musicRepository.getCachedSongs(musicList, cachedSongs)
				cacheSongs(allSongs)
				Log.i("DataInfo", "All songs: ${allSongs.size} | Songs fetched: $songsFetched")
			}
		}
	}

	private fun restoreCurrentPlaylist() {
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
	}

	private fun getAlbums() {
		viewModelScope.launch {
			mediaRepository.getAlbums().collect {
				albumList = it
			}
		}
	}

	private fun getArtists() {
		viewModelScope.launch {
			mediaRepository.getArtists().collect {
				artistList = it
			}
		}
	}

	private fun getCurrentSong() {
		viewModelScope.launch {
			nowPlaying.collect { item ->
				musicItem(item)?.let { currentSong = it }
				delay(3000) // delay allows songs to completely load from device before further action
				musicItem(item)?.let { currentSong = it; saveStreamDate(it) }
				updateMusicQueue(queueEdited = false)
			}
		}
	}

	private fun getNowPlayingMetadata() {
		viewModelScope.launch {
			nowPlayingMetadata.collect {
				if (!songsFetched) return@collect
				if (currentSong !in songs) currentSong = musicItem(it)
			}
		}
	}

	fun musicItem(mediaItem: MediaItem?): Music? {
		val currentSongUri = mediaItem?.localConfiguration?.uri
		val songPath = currentSongUri.toString()
			.replace("%20", " ")
			.replace("%40", "@")
		return cachedSongs.find {
			it.uri == currentSongUri || songPath.contains(it.path)
		}
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

	fun preparePlaylist() {
		mediaSession?.player?.apply {
			clearMediaItems()
			setMediaItems(mediaItems.value)
			prepare()
		}
	}

	fun playNext(song: List<Music>) {
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

	fun addToQueue(song: List<Music>) {
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

	fun favoriteSong(song: Music) {
		viewModelScope.launch(Dispatchers.IO) {
			song.apply { favorite = !favorite }
			musicRepository.updateSongInCache(song)
		}
	}

	fun createPlaylist(playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistRepository.createPlaylist(playlist)
		}
	}

	fun shareSong(context: Context, songs: List<Music>) {
		mediaRepository.shareSong(context, songs)
	}

	private fun saveStreamDate(song: Music) {
		viewModelScope.launch(Dispatchers.IO) {
			song.lastPlayed = Calendar.getInstance().time
			musicRepository.updateSongInCache(song)
		}
	}

	fun fetchPlaylistSongs(playlistName: String) {
		val playlist = playlists.find { it.name == playlistName }
		val uris = playlist?.songs
		songsFromPlaylist = songs.filter { uris?.contains(it.uri) == true }
		Log.i("ViewModelInfo", "Songs from playlist: $songsFromPlaylist")
	}

	fun updateMusicQueue(queueEdited: Boolean = true) {
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
		Log.i("ViewModelInfo", "queueAction: $action")
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

	private fun cacheSongs(songs: List<Music>) {
		viewModelScope.launch(Dispatchers.IO) {
			musicRepository.cacheSongs(songs)
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