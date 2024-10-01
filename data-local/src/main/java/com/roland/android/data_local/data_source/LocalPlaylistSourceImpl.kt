package com.roland.android.data_local.data_source

import com.roland.android.data_local.database.MusicDao
import com.roland.android.data_local.database.PlaylistDao
import com.roland.android.data_local.util.Converters.convertToMusic
import com.roland.android.data_local.util.Converters.convertToPlaylist
import com.roland.android.data_repository.data_source.local.LocalPlaylistSource
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalPlaylistSourceImpl : LocalPlaylistSource, KoinComponent {
	private val musicDao by inject<MusicDao>()
	private val playlistDao by inject<PlaylistDao>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun getAllPlaylists(): Flow<List<Playlist>> {
		return playlistDao.getAllPlaylists()
			.map { playlistEntityList ->
				playlistEntityList.map { it.convertToPlaylist() }
			}
	}

	override fun getSongsFromPlaylist(playlistName: String): Flow<List<Music>> {
		var playlistSongs = emptyList<Music>()
		coroutineScope.launch {
			val allSongs = musicDao.getAllSongs().last()
			val playlist = playlistDao.getPlaylist(playlistName).last()
			playlistSongs = allSongs
				.filter { playlist.songs.contains(it.uri) }
				.map { it.convertToMusic() }
		}
		return flowOf(playlistSongs)
	}
}