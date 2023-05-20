package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MediaViewModel(
	appDataStore: AppDataStore,
	private val repository: MediaRepository
) : BaseMediaViewModel(appDataStore, repository) {
	var albumList by mutableStateOf<List<Album>>(emptyList()); private set
	var artistList by mutableStateOf<List<Artist>>(emptyList()); private set

	var searchQuery by mutableStateOf("")

	init {
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

	fun resetPlaylist(newPlaylist: List<Music>) {
		mediaItems.value = newPlaylist.map { it.uri.toMediaItem }.toMutableList()
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
}