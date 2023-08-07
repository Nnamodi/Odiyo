package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaItemsUiState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.mediaUiState
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.navigation.*
import com.roland.android.odiyo.util.AudioIntentActions
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SongDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.Q)
class MediaViewModel @Inject constructor(
	private val appDataStore: AppDataStore,
	private val musicRepository: MusicRepository,
	private val mediaRepository: MediaRepository,
	private val playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {

	init {
		viewModelScope.launch {
			appDataStore.getSortPreference().collectLatest { option ->
				sortOrder = option
				songs = songs.sortList()
				favoriteSongs = favoriteSongs.sortList()
				mediaUiState.update { it.copy(allSongs = songs, sortOption = option) }
				mediaItemsUiState.update { it.copy(sortOption = option) }
			}
		}
	}

	fun resetPlaylist(newPlaylist: List<Music>) {
		mediaItems.value = newPlaylist.map { it.uri.toMediaItem }.toMutableList()
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
		if (!canAccessStorage) return
		when (action) {
			is MediaMenuActions.PlayNext -> playNext(action.songs)
			is MediaMenuActions.AddToQueue -> addToQueue(action.songs)
			is MediaMenuActions.RenameSong -> renameSong(action.details)
			is MediaMenuActions.Favorite -> favoriteSong(action.song)
			is MediaMenuActions.CreatePlaylist -> createPlaylist(action.playlist)
			is MediaMenuActions.AddToPlaylist -> addSongsToPlaylist(action.songs, action.playlist)
			is MediaMenuActions.RemoveFromPlaylist -> removeFromPlaylist(action.songs, action.playlistName)
			is MediaMenuActions.SetAsRingtone -> setAsRingtone(context, action.music)
			is MediaMenuActions.ShareSong -> shareSong(context, action.songs)
			is MediaMenuActions.SortSongs -> sortSongs(action.sortOptions)
			is MediaMenuActions.DeleteSongs -> deleteSong(action.songs)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "menuAction: $action")
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

	private fun removeFromPlaylist(songs: List<Music>, playlistName: String) {
		viewModelScope.launch(Dispatchers.IO) {
			val playlist = mediaScreenUiState.playlists.find { it.name == playlistName }
			playlist?.let {
				val updatedSongs = it.songs.toMutableList()
				updatedSongs.removeAll(songs.map { music -> music.uri })
				it.songs = updatedSongs
				playlistRepository.updatePlaylist(it)
				fetchPlaylistSongs(playlistName)
				Log.d("ViewModelInfo", "Song removed: ${it.songs}")
			}
		}
	}

	private fun sortSongs(sortOption: SortOptions) {
		viewModelScope.launch(Dispatchers.IO) {
			appDataStore.saveSortPreference(sortOption)
		}
	}

	private fun deleteSong(songsToDelete: List<SongDetails>) {
		songsToDelete.forEach { song ->
			val songDetails = SongDetails(song.id, song.uri)
			val songToDelete = songs.find { it.id == songDetails.id }
			mediaRepository.deleteSongFromSystem(songDetails)
			if (nowPlayingScreenUiState.musicQueue.contains(songToDelete)) {
				mediaItems.value.removeAll { it == songToDelete?.uri?.toMediaItem }
			}
			viewModelScope.launch(Dispatchers.IO) {
				val music = songs.find { it.id == songDetails.id }
				music?.let { musicRepository.deleteSongFromCache(music) }
			}
		}
	}

	private fun songsFromAlbum(albumName: String): List<Music> {
		var songsFromAlbum by mutableStateOf<List<Music>>(emptyList())
		viewModelScope.launch {
			mediaRepository.getSongsFromAlbum(
				arrayOf(albumName)
			).collect { songs ->
				val songList = songs.map { Music(it.id, it.uri, it.name, it.title, it.artist, it.time, it.bytes, it.addedOn, it.album, it.path) }
				songsFromAlbum = songList
					.filter { it.name.endsWith(".mp3") }
					.sortList()
			}
		}
		return songsFromAlbum.filter { song ->
			song.id in songs.map { it.id }
		}.map { song ->
			songs.find { it.id == song.id }!!
		}
	}

	private fun songsFromArtist(artistName: String): List<Music> {
		var songsFromArtist by mutableStateOf<List<Music>>(emptyList())
		viewModelScope.launch {
			mediaRepository.getSongsFromArtist(
				arrayOf(artistName)
			).collect { songs ->
				val songList = songs.map { Music(it.id, it.uri, it.name, it.title, it.artist, it.time, it.bytes, it.addedOn, it.album, it.path) }
				songsFromArtist = songList
					.filter { it.name.endsWith(".mp3") }
					.sortList()
			}
		}
		return songsFromArtist.filter { song ->
			song.id in songs.map { it.id }
		}.map { song ->
			songs.find { it.id == song.id }!!
		}
	}

	fun prepareMediaItems(collectionName: String, collectionType: String) {
		if (collectionType == PLAYLISTS) { fetchPlaylistSongs(collectionName); return }
		val songs = when (collectionType) {
			ALBUMS -> songsFromAlbum(collectionName)
			ARTISTS -> songsFromArtist(collectionName)
			FAVORITES -> favoriteSongs
			LAST_PLAYED -> lastPlayedSongs
			ADD_TO_PLAYLIST -> songsToAddToPlaylist(collectionName)
			else -> emptyList()
		}
		mediaItemsUiState.update {
			it.copy(songs = songs, collectionName = collectionName, collectionType = collectionType)
		}
	}

	fun editSearchQuery(query: String?, clearQuery: Boolean) {
		query?.let { text -> mediaItemsUiState.update { it.copy(searchQuery = text) } }
		if (clearQuery) mediaItemsUiState.update { it.copy(searchQuery = "") }
		songsFromSearch()
	}

	private fun songsFromSearch() {
		val result = songs.filter { music ->
			val matchingCombinations = listOf(
				"${music.artist}${music.title}",
				"${music.artist} ${music.title}",
				"${music.title}${music.artist}",
				"${music.title} ${music.artist}",
				music.title, music.artist, music.album
			)
			matchingCombinations.any { it.contains(mediaItemsScreenUiState.searchQuery, ignoreCase = true) }
		}
		mediaItemsUiState.update { it.copy(songs = result) }
	}

	private fun songsToAddToPlaylist(playlistName: String?): List<Music> {
		val playlist = mediaScreenUiState.playlists.find { it.name == playlistName }
		return songs.filterNot { playlist?.songs?.contains(it.uri) == true }
	}

	fun audioIntentAction(action: AudioIntentActions) {
		if (!canAccessStorage) return
		when (action) {
			is AudioIntentActions.Play -> playAudioFromIntent(action.uri)
			is AudioIntentActions.PlayNext -> playNext(action.uri)
			is AudioIntentActions.AddToQueue -> addToQueue(action.uri)
		}
		updateMusicQueue()
		Log.d("ViewModelInfo", "audioIntentAction: $action")
	}

	fun playAudioFromIntent(uri: Uri) {
		mediaItems.value = mutableListOf(uri.toMediaItem)
		playAudio(uri, 0)
	}

	private fun playNext(uri: Uri) {
		val mediaItem = uri.toMediaItem
		mediaSession?.player?.apply {
			if (nowPlayingScreenUiState.musicQueue.isNotEmpty()) {
				val index = currentMediaItemIndex + 1
				addMediaItem(index, mediaItem)
				mediaItems.value.add(index, mediaItem)
			} else {
				pause()
				mediaItems.value = mutableListOf(mediaItem)
				preparePlaylist()
			}
		}
	}

	private fun addToQueue(uri: Uri) {
		val mediaItem = uri.toMediaItem
		mediaSession?.player?.apply {
			if (nowPlayingScreenUiState.musicQueue.isNotEmpty()) {
				addMediaItem(mediaItem)
				mediaItems.value.add(mediaItem)
			} else {
				pause()
				mediaItems.value = mutableListOf(mediaItem)
				preparePlaylist()
			}
		}
	}
}