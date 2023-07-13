package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
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
import com.roland.android.odiyo.service.Util.EMPTY_MEDIA_ITEM
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.currentMediaIndex
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.readStoragePermissionGranted
import com.roland.android.odiyo.service.Util.songsOnQueue
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.states.MediaUiState
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
open class BaseMediaViewModel(
	private val appDataStore: AppDataStore,
	private val mediaRepository: MediaRepository,
	private val musicRepository: MusicRepository,
	private val playlistRepository: PlaylistRepository
) : ViewModel() {
	private var cachedSongs by mutableStateOf<List<Music>>(emptyList())
	var songs by mutableStateOf<List<Music>>(emptyList())
	var lastPlayedSongs by mutableStateOf<List<Music>>(emptyList()); private set
	var favoriteSongs by mutableStateOf<List<Music>>(emptyList())
	var recentSongs by mutableStateOf<List<Music>>(emptyList()); private set

	var currentMediaItems by mutableStateOf<List<MediaItem>>(emptyList()); private set
	var sortOrder by mutableStateOf(SortOptions.NameAZ)
	var songsFetched by mutableStateOf(false)
	var canAccessStorage by mutableStateOf(false)

	val mediaUiState = MutableStateFlow(MediaUiState())
	private val _mediaUiState: StateFlow<MediaUiState> = mediaUiState.asStateFlow()
	var mediaScreenUiState by mutableStateOf(MediaUiState()); private set

	val mediaItemsUiState = MutableStateFlow(MediaItemsUiState())
	private val _mediaItemsUiState: StateFlow<MediaItemsUiState> = mediaItemsUiState.asStateFlow()
	var mediaItemsScreenUiState by mutableStateOf(MediaItemsUiState()); private set

	val nowPlayingUiState = MutableStateFlow(NowPlayingUiState())
	private val _nowPlayingUiState: StateFlow<NowPlayingUiState> = nowPlayingUiState.asStateFlow()
	var nowPlayingScreenUiState by mutableStateOf(NowPlayingUiState()); private set

	init {
		viewModelScope.launch {
			playlistRepository.getPlaylists.collectLatest { allPlaylists ->
				mediaUiState.update { it.copy(playlists = allPlaylists) }
				mediaItemsUiState.update { it.copy(playlists = allPlaylists) }
				nowPlayingUiState.update { it.copy(playlists = allPlaylists) }
			}
		}
		viewModelScope.launch {
			songsOnQueue.collect { queue ->
				nowPlayingUiState.update { it.copy(musicQueue = queue) }
			}
		}
		viewModelScope.launch {
			playingState.collect { playingState ->
				nowPlayingUiState.update { it.copy(playingState = playingState) }
			}
		}
		viewModelScope.launch {
			currentMediaIndex.collect { index ->
				nowPlayingUiState.update { it.copy(currentSongIndex = index) }
			}
		}
		viewModelScope.launch {
			_mediaUiState.collectLatest {
				mediaScreenUiState = it
			}
		}
		viewModelScope.launch {
			_mediaItemsUiState.collectLatest {
				mediaItemsScreenUiState = it
			}
		}
		viewModelScope.launch {
			_nowPlayingUiState.collectLatest {
				nowPlayingScreenUiState = it
			}
		}
		viewModelScope.launch {
			readStoragePermissionGranted.collectLatest {
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
				mediaUiState.update { it.copy(songs = songs) }
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
					val items = it.playlist.map { item -> item.toUri().toMediaItem }.toMutableList()
					mediaItems.value = if (items.size == 1 && items[0] == EMPTY_MEDIA_ITEM) mutableListOf() else items
					mediaSession?.player?.apply {
						setMediaItems(mediaItems.value); prepare()
						if (mediaItems.value.isNotEmpty()) {
							seekTo(it.currentSongPosition, it.currentSongSeekPosition)
						}
					}
					currentMediaItems = mediaItems.value
					Log.i("ViewModelInfo", "CurrentPlaylist: ${it.playlist.take(15)}, ${it.currentSongPosition}")
				}
			}
		}
	}

	private fun getAlbums() {
		viewModelScope.launch {
			mediaRepository.getAlbums().collect { albums ->
				mediaUiState.update { it.copy(albums = albums) }
			}
		}
	}

	private fun getArtists() {
		viewModelScope.launch {
			mediaRepository.getArtists().collect { artists ->
				mediaUiState.update { it.copy(artists = artists) }
			}
		}
	}

	private fun getCurrentSong() {
		viewModelScope.launch {
			nowPlaying.collect { item ->
				musicItem(item)?.let { music ->
					nowPlayingUiState.update { it.copy(currentSong = music) }
				}
				delay(3000) // delay allows songs to completely load from device before further action
				musicItem(item)?.let { music ->
					updateMusicInfo(music)
					val currentMediaItem = music.uri.toMediaItem
					mediaUiState.update { it.copy(currentMediaItem = currentMediaItem) }
					mediaItemsUiState.update { it.copy(currentMediaItem = currentMediaItem) }
					nowPlayingUiState.update { it.copy(currentSong = music) }
				}
				updateMusicQueue(queueEdited = false)
				if (nowPlayingScreenUiState.currentSong == null) getNowPlayingMetadata()
				if (mediaItems.value.isEmpty()) nowPlayingUiState.update { it.copy(currentSong = null) }
			}
		}
	}

	private fun getNowPlayingMetadata() {
		viewModelScope.launch {
			nowPlayingMetadata.collect { metadata ->
				if (!songsFetched && nowPlayingScreenUiState.currentSong in songs) return@collect
				val currentMediaItem = musicItem(metadata).uri.toMediaItem
				mediaUiState.update { it.copy(currentMediaItem = currentMediaItem) }
				mediaItemsUiState.update { it.copy(currentMediaItem = currentMediaItem) }
				nowPlayingUiState.update { it.copy(currentSong = musicItem(metadata)) }
			}
		}
	}

	fun musicItem(mediaItem: MediaItem?): Music? {
		val songUri = mediaItem?.localConfiguration?.uri
		val songPath = URLDecoder.decode(songUri.toString(), UTF_8.name())
		return cachedSongs.find {
			it.uri == songUri || songPath.contains(it.path)
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

	fun updateMediaArtwork(artwork: Bitmap) {
		nowPlayingUiState.update { it.copy(artwork = artwork) }
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
			if (nowPlayingScreenUiState.musicQueue.isNotEmpty()) {
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
			if (nowPlayingScreenUiState.musicQueue.isNotEmpty()) {
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

	fun addSongsToPlaylist(songsToAdd: List<Music>, playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			if (songsToAdd.isNotEmpty()) {
				val uriList = playlist.songs.toMutableList()
				uriList.addAll(0, songsToAdd.map { it.uri })
				playlist.songs = uriList
				playlistRepository.updatePlaylist(playlist)
				fetchPlaylistSongs(playlist.name)
				Log.d("ViewModelInfo", "Song added: ${playlist.songs}")
			} else {
				playlistRepository.createPlaylist(playlist)
			}
		}
	}

	fun shareSong(context: Context, songs: List<Music>) {
		mediaRepository.shareSong(context, songs)
	}

	private fun updateMusicInfo(song: Music) {
		viewModelScope.launch(Dispatchers.IO) {
			song.lastPlayed = Calendar.getInstance().time
			musicRepository.updateSongInCache(song)
		}
	}

	fun fetchPlaylistSongs(playlistName: String) {
		val playlist = mediaScreenUiState.playlists.find { it.name == playlistName }
		val uris = playlist?.songs
		val songsFromPlaylist = songs.filter { uris?.contains(it.uri) == true }
		mediaItemsUiState.update {
			it.copy(songs = songsFromPlaylist, collectionName = playlistName, collectionType = PLAYLISTS)
		}
		Log.i("ViewModelInfo", "Songs from playlist: $songsFromPlaylist")
	}

	fun updateMusicQueue(queueEdited: Boolean = true) {
		songsOnQueue.value = mediaItems.value.map {
			musicItem(it) ?: musicItem(it.mediaMetadata)
		}.toMutableList()
		if (queueEdited || mediaItems.value.isNotEmpty()) saveCurrentPlaylist()
	}

	fun queueAction(action: QueueItemActions) {
		when (action) {
			is QueueItemActions.Play -> playFromQueue(action.item)
			is QueueItemActions.DuplicateSong -> duplicateSong(action.item)
			is QueueItemActions.RemoveSong -> removeSong(action.item)
			is QueueItemActions.CreatePlaylist -> createPlaylist(action.playlist)
			is QueueItemActions.AddToPlaylist -> addSongsToPlaylist(action.songs, action.playlist)
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
		if (mediaItems.value.isNotEmpty()) return
		mediaUiState.update { it.copy(currentMediaItem = NOTHING_PLAYING) }
		mediaItemsUiState.update { it.copy(currentMediaItem = NOTHING_PLAYING) }
		nowPlayingUiState.update { it.copy(currentSong = null) }
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