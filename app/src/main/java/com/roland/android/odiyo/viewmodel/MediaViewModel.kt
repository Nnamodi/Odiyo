package com.roland.android.odiyo.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SongDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MediaViewModel @Inject constructor(
	private val appDataStore: AppDataStore,
	private val musicRepository: MusicRepository,
	private val mediaRepository: MediaRepository,
	private val playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {
	var searchQuery by mutableStateOf("")

	init {
		viewModelScope.launch {
			appDataStore.getSortPreference().collectLatest {
				sortOrder = it
				songs = songs.sortList()
				favoriteSongs = favoriteSongs.sortList()
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
		when (action) {
			is MediaMenuActions.PlayNext -> playNext(action.songs)
			is MediaMenuActions.AddToQueue -> addToQueue(action.songs)
			is MediaMenuActions.RenameSong -> renameSong(action.details)
			is MediaMenuActions.Favorite -> favoriteSong(action.song)
			is MediaMenuActions.CreatePlaylist -> createPlaylist(action.playlist)
			is MediaMenuActions.AddToPlaylist -> addSongsToPlaylist(action.songs, action.playlist)
			is MediaMenuActions.RemoveFromPlaylist -> removeFromPlaylist(action.song, action.playlistName)
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

	private fun addSongsToPlaylist(songsToAdd: List<Music>, playlist: Playlist) {
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

	private fun removeFromPlaylist(song: Music, playlistName: String) {
		viewModelScope.launch(Dispatchers.IO) {
			val playlist = playlists.find { it.name == playlistName }
			playlist?.let {
				val updatedSongs = it.songs
				updatedSongs.toMutableList().remove(song.uri)
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
			if (musicQueue.contains(songToDelete)) {
				mediaItems.value.removeAll { it == songToDelete?.uri?.toMediaItem }
			}
			viewModelScope.launch(Dispatchers.IO) {
				val music = songs.find { it.id == songDetails.id }
				music?.let { musicRepository.deleteSongFromCache(music) }
			}
		}
	}

	fun songsFromAlbum(albumName: String): List<Music> {
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

	fun songsFromArtist(artistName: String): List<Music> {
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

	fun songsFromSearch(): List<Music> {
		val result = songs.filter { music ->
			val matchingCombinations = listOf(
				"${music.artist}${music.title}",
				"${music.artist} ${music.title}",
				"${music.title}${music.artist}",
				"${music.title} ${music.artist}",
				music.title, music.artist, music.album
			)
			matchingCombinations.any { it.contains(searchQuery, ignoreCase = true) }
		}
		return result
	}
}