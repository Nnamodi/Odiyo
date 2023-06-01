package com.roland.android.odiyo.repository

import com.roland.android.odiyo.database.MusicDao
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.flow.Flow

class MusicRepository(private val musicDao: MusicDao) {
	fun getCachedSongs(): Flow<List<Music>> = musicDao.getAllSongs()

	suspend fun getCachedSongs(songsFromSystem: List<Music>): List<Music> {
		var allSongs: List<Music> = emptyList()
		musicDao.getAllSongs().collect { musicList ->
			val songsFromDatabase = musicList.takeWhile { music ->
				songsFromSystem.map { it.id }.contains(music.id)
			}
			val newSongs = songsFromSystem.takeWhile { song ->
				!songsFromDatabase.map { it.id }.contains(song.id)
			}
			allSongs = songsFromDatabase.plus(newSongs)
		}
		return allSongs
	}

	suspend fun cacheSongs(songs: List<Music>) {
		musicDao.addSongs(songs)
	}

	suspend fun updateSongInCache(song: Music) {
		musicDao.updateSong(song)
	}

	suspend fun deleteSongFromCache(song: Music) {
		musicDao.deleteSong(song)
	}
}