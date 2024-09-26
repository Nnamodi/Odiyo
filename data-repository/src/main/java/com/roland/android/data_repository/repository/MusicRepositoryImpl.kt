package com.roland.android.data_repository.repository

import android.util.Log
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_source.system.SystemMusicSource
import com.roland.android.data_repository.data_util.system.PlayerUtil
import com.roland.android.data_repository.util.Converters.convertToMusic
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicRepositoryImpl : MusicRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val systemMusicSource by inject<SystemMusicSource>()
	private val playerUtil by inject<PlayerUtil>()
	private val coroutineScope by inject<CoroutineScope>()
	private var fetchedFromSystem = false

	override fun getAllSongs(): Flow<List<Music>> {
		if (fetchedFromSystem) {
			return localMusicSource.getAllSongs()
		}

		var allSongsList = flowOf(emptyList<Music>())
		coroutineScope.launch {
			val cachedSongs = localMusicSource.getAllSongs().last()
			val songsFromSystem = systemMusicSource.getAllSongs().last()
			val songsFromDatabase = cachedSongs.filter { music ->
				songsFromSystem.map { it.id }.contains(music.id)
			}
			val newSongs = songsFromSystem.filterNot { song ->
				songsFromDatabase.map { it.id }.contains(song.id)
			}.map {
				it.convertToMusic()
			}
			val allSongs = songsFromDatabase.plus(newSongs)
			fetchedFromSystem = true
			Log.i(
				/* tag = */ "DataInfo",
				/* msg = */ "${cachedSongs.size}, ${newSongs.size} | ${allSongs.size} | ${songsFromSystem.size}"
			)
			allSongsList = flowOf(allSongs)
		}
		return allSongsList
	}

	override fun getLastPlayedSongs(): Flow<List<Music>> {
		return localMusicSource.getLastPlayedSongs()
	}

	override fun getRecentlyAddedSongs(): Flow<List<Music>> {
		return localMusicSource.getRecentlyAddedSongs()
	}

	override fun getFavoriteSongs(): Flow<List<Music>> {
		return localMusicSource.getFavoriteSongs()
	}

	override fun getCurrentSong(): Flow<Music> {
		return playerUtil.getCurrentSong()
	}

	override fun getAlbums(): Flow<List<Album>> {
		return systemMusicSource.getAlbums()
	}

	override fun getArtists(): Flow<List<Artist>> {
		return systemMusicSource.getArtists()
	}

	override fun getSongsFromAlbum(selectionArgs: Array<String>): Flow<List<Music>> {
		return systemMusicSource.getSongsFromAlbum(selectionArgs)
			.map { systemList ->
				systemList.map { it.convertToMusic() }
			}
	}

	override fun getSongsFromArtist(selectionArgs: Array<String>): Flow<List<Music>> {
		return systemMusicSource.getSongsFromArtist(selectionArgs)
			.map { systemList ->
				systemList.map { it.convertToMusic() }
			}
	}
}