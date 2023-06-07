package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MediaViewModel(
	appDataStore: AppDataStore,
	musicRepository: MusicRepository,
	private val mediaRepository: MediaRepository,
	playlistRepository: PlaylistRepository
) : BaseMediaViewModel(appDataStore, mediaRepository, musicRepository, playlistRepository) {
	var albumList by mutableStateOf<List<Album>>(emptyList()); private set
	var artistList by mutableStateOf<List<Artist>>(emptyList()); private set

	var searchQuery by mutableStateOf("")

	init {
		viewModelScope.launch {
			mediaRepository.getAlbums.collect {
				albumList = it
			}
		}
		viewModelScope.launch {
			mediaRepository.getArtists.collect {
				artistList = it
			}
		}
	}

	fun resetPlaylist(newPlaylist: List<Music>) {
		mediaItems.value = newPlaylist.map { it.uri.toMediaItem }.toMutableList()
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

	fun songsFromPlaylist(urisString: String): List<Music> {
		val uris = urisString.split("|").map { it.toUri() }
		return songs.filter { uris.contains(it.uri) }
	}
}