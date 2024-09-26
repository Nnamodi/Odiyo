package com.roland.android.data_repository.repository

import com.roland.android.data_repository.data_source.local.LocalPlaylistSource
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistRepositoryImpl : PlaylistRepository, KoinComponent {
	private val localPlaylistSource by inject<LocalPlaylistSource>()

	override fun getAllPlaylists(): Flow<List<Playlist>> {
		return localPlaylistSource.getAllPlaylists()
	}

	override fun getSongsFromPlaylist(playlistName: String): Flow<List<Music>> {
		return localPlaylistSource.getSongsFromPlaylist(playlistName)
	}
}