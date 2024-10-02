package com.roland.android.data_repository.repository

import android.util.Log
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_source.system.SystemMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.system.SystemPlayerUtil
import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.data_repository.util.Converters.convertToMusic
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicRepositoryImpl : MusicRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val systemMusicSource by inject<SystemMusicSource>()
	private val systemPlayerUtil by inject<SystemPlayerUtil>()
	private val coroutineScope by inject<CoroutineScope>()
	private var fetchedFromSystem = false

	override fun getAllSongs(): Flow<List<Music>> {
		if (fetchedFromSystem) {
			return localMusicSource.getAllSongs()
		}

		var allSongsList = flowOf(emptyList<Music>())
		coroutineScope.launch {
			combine(
				localMusicSource.getAllSongs(),
				systemMusicSource.getAllSongs()
			) { cachedSongs, songsFromSystem ->
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

				syncDatabaseWithSystem(cachedSongs, songsFromSystem)
			}
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
		return systemPlayerUtil.getCurrentSong()
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

	private fun syncDatabaseWithSystem(
		songsFromDatabase: List<Music>,
		songsFromSystem: List<MusicFromSystem>
	) {
		val newSongs = songsFromSystem.filterNot { song ->
			songsFromDatabase.map { it.id }.contains(song.id)
		}.map {
			it.convertToMusic()
		}
		val removedSongs = songsFromDatabase.filterNot { song ->
			songsFromSystem.map { it.id }.contains(song.id)
		}

		if (newSongs.isNotEmpty()) {
			localMusicUtil.addNewSongs(newSongs)
		}
		if (removedSongs.isNotEmpty()) {
			removedSongs.forEach {
				localMusicUtil.deleteSong(it)
			}
		}
	}
}