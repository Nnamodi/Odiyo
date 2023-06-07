package com.roland.android.odiyo.repository

import com.roland.android.odiyo.database.PlaylistDao
import com.roland.android.odiyo.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {
	val getPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()

	suspend fun createPlaylist(playlist: Playlist) {
		playlistDao.createPlaylist(playlist)
	}

	suspend fun updatePlaylist(playlist: Playlist) {
		playlistDao.updatePlaylist(playlist)
	}

	suspend fun deletePlaylist(playlist: Playlist) {
		playlistDao.deletePlaylist(playlist)
	}
}