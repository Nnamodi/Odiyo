package com.roland.android.odiyo.repository

import com.roland.android.odiyo.database.PlaylistDao
import com.roland.android.odiyo.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(playlistDao: PlaylistDao) {
	val getPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()
}