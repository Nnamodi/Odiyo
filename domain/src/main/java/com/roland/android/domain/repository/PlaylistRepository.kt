package com.roland.android.domain.repository

import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

	fun getAllPlaylists(): Flow<List<Playlist>>

	fun getSongsFromPlaylist(playlistName: String): Flow<List<Music>>

}