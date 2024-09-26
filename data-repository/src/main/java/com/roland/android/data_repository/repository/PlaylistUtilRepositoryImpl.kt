package com.roland.android.data_repository.repository

import com.roland.android.data_repository.data_util.local.LocalPlaylistUtil
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.PlaylistUtilRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistUtilRepositoryImpl : PlaylistUtilRepository, KoinComponent {
	private val localPlaylist by inject<LocalPlaylistUtil>()

	override fun createPlaylist(playlist: Playlist) {
		localPlaylist.createPlaylist(playlist)
	}

	override fun updatePlaylist(playlist: Playlist) {
		localPlaylist.updatePlaylist(playlist)
	}

	override fun deletePlaylist(playlist: Playlist) {
		localPlaylist.deletePlaylist(playlist)
	}
}