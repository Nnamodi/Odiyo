package com.roland.android.data_repository.data_source.local

import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface LocalPlaylistSource {

	fun getAllPlaylists(): Flow<List<Playlist>>

	fun getSongsFromPlaylist(playlistName: String): Flow<List<Music>>

}