package com.roland.android.odiyo.repository

import android.util.Log
import com.roland.android.odiyo.database.MusicDao
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.flow.Flow

class MusicRepository(private val musicDao: MusicDao) {
	val getCachedSongs: Flow<List<Music>> = musicDao.getAllSongs()

	fun getCachedSongs(songsFromSystem: List<Music>, cachedSongs: List<Music>): List<Music> {
		val songsFromDatabase = cachedSongs.filter { music ->
			songsFromSystem.map { it.id }.contains(music.id)
		}
		val newSongs = songsFromSystem.filterNot { song ->
			songsFromDatabase.map { it.id }.contains(song.id)
		}
		val allSongs = songsFromDatabase.plus(newSongs)
		Log.i("DataInfo", "${cachedSongs.size}, ${newSongs.size} | ${allSongs.size} | ${songsFromSystem.size}")
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

	suspend fun clear() {
		musicDao.clearDatabase()
	}
}