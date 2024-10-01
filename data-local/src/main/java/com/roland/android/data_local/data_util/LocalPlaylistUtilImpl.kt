package com.roland.android.data_local.data_util

import com.roland.android.data_local.database.PlaylistDao
import com.roland.android.data_local.util.Converters.convertToPlaylistEntity
import com.roland.android.data_repository.data_util.local.LocalPlaylistUtil
import com.roland.android.domain.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalPlaylistUtilImpl : LocalPlaylistUtil, KoinComponent {
	private val playlistDao by inject<PlaylistDao>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun createPlaylist(playlist: Playlist) {
		coroutineScope.launch {
			val playlistEntity = playlist.convertToPlaylistEntity()
			playlistDao.createPlaylist(playlistEntity)
		}
	}

	override fun updatePlaylist(playlist: Playlist) {
		coroutineScope.launch {
			val playlistEntity = playlist.convertToPlaylistEntity()
			playlistDao.updatePlaylist(playlistEntity)
		}
	}

	override fun deletePlaylist(playlist: Playlist) {
		coroutineScope.launch {
			val playlistEntity = playlist.convertToPlaylistEntity()
			playlistDao.deletePlaylist(playlistEntity)
		}
	}
}