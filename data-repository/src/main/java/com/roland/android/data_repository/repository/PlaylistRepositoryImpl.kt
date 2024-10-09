package com.roland.android.data_repository.repository

import android.content.Context
import com.roland.android.data_repository.data_source.local.LocalPlaylistSource
import com.roland.android.data_repository.util.Converters.includeArtwork
import com.roland.android.data_repository.util.Converters.includeArtworks
import com.roland.android.data_repository.util.Extensions.getBitmap
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistRepositoryImpl(
	private val context: Context
) : PlaylistRepository, KoinComponent {
	private val localPlaylistSource by inject<LocalPlaylistSource>()

	override fun getAllPlaylists(): Flow<List<Playlist>> {
		return localPlaylistSource.getAllPlaylists()
			.map { playlists ->
				playlists.map {
					val artwork = it.getBitmap(context)
					it.includeArtwork(artwork)
				}
			}
	}

	override fun getSongsFromPlaylist(playlistName: String): Flow<List<Music>> {
		return localPlaylistSource.getSongsFromPlaylist(playlistName)
			.includeArtworks(context)
	}
}