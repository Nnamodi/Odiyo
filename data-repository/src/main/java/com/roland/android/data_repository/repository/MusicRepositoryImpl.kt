package com.roland.android.data_repository.repository

import android.content.Context
import android.util.Log
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_source.system.SystemMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.player.SystemPlayerUtil
import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.data_repository.util.Converters.convertToMusic
import com.roland.android.data_repository.util.Converters.includeArtwork
import com.roland.android.data_repository.util.Converters.includeArtworks
import com.roland.android.data_repository.util.Extensions.getBitmap
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicRepositoryImpl(
	private val context: Context
) : MusicRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val systemMusicSource by inject<SystemMusicSource>()
	private val systemPlayerUtil by inject<SystemPlayerUtil>()
	private var fetchedFromSystem = false

	override fun getAllSongs(): Flow<List<Music>> {
		if (fetchedFromSystem) {
			return localMusicSource.getAllSongs()
				.includeArtworks(context)
		}

		return combine(
			localMusicSource.getAllSongs(),
			systemMusicSource.getAllSongs()
		) { cachedSongs, songsFromSystem ->
			syncDatabaseWithSystem(cachedSongs, songsFromSystem)

			val songsFromDatabase = cachedSongs.filter { music ->
				songsFromSystem.map { it.id }.contains(music.id)
			}
			val newSongs = songsFromSystem.filterNot { song ->
				songsFromDatabase.map { it.id }.contains(song.id)
			}.map {
				it.convertToMusic()
			}
			val allSongs = songsFromDatabase.plus(newSongs)
			Log.i("DataInfo", "${cachedSongs.size}, ${newSongs.size} | ${allSongs.size} | ${songsFromSystem.size}")

			fetchedFromSystem = true
			allSongs
		}.includeArtworks(context)
	}

	override fun getLastPlayedSongs(): Flow<List<Music>> {
		return localMusicSource.getLastPlayedSongs()
			.includeArtworks(context)
	}

	override fun getRecentlyAddedSongs(): Flow<List<Music>> {
		return localMusicSource.getRecentlyAddedSongs()
			.includeArtworks(context)
	}

	override fun getFavoriteSongs(): Flow<List<Music>> {
		return localMusicSource.getFavoriteSongs()
			.includeArtworks(context)
	}

	override fun getCurrentSong(): Flow<Music?> {
		return systemPlayerUtil.getCurrentSong()
			.map { song ->
				song?.let {
					val artwork = song.getBitmap(context)
					song.includeArtwork(artwork)
				}
			}
	}

	override fun getAlbums(): Flow<List<Album>> {
		return systemMusicSource.getAlbums().includeAlbumArtworks()
	}

	override fun getArtists(): Flow<List<Artist>> {
		return systemMusicSource.getArtists().includeArtistArtworks()
	}

	override fun getSongsFromAlbum(selectionArgs: Array<String>): Flow<List<Music>> {
		return systemMusicSource.getSongsFromAlbum(selectionArgs)
			.map { systemList ->
				systemList.map { it.convertToMusic() }
			}
			.includeArtworks(context)
	}

	override fun getSongsFromArtist(selectionArgs: Array<String>): Flow<List<Music>> {
		return systemMusicSource.getSongsFromArtist(selectionArgs)
			.map { systemList ->
				systemList.map { it.convertToMusic() }
			}
			.includeArtworks(context)
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

	private fun Flow<List<Album>>.includeAlbumArtworks(): Flow<List<Album>> {
		return map { albums ->
			albums.map {
				val artwork = it.getBitmap(context)
				it.includeArtwork(artwork)
			}
		}
	}

	private fun Flow<List<Artist>>.includeArtistArtworks(): Flow<List<Artist>> {
		return map { artists ->
			artists.map {
				val artwork = it.getBitmap(context)
				it.includeArtwork(artwork)
			}
		}
	}
}