package com.roland.android.odiyo.ui.screens.playlists

import com.roland.android.domain.model.Playlist

sealed interface PlaylistMenuActions {

	data class CreatePlaylist(val playlist: Playlist) : PlaylistMenuActions

	data class PlayNext(val playlist: Playlist) : PlaylistMenuActions

	data class AddToQueue(val playlist: Playlist) : PlaylistMenuActions

	data class RenamePlaylist(val playlist: Playlist): PlaylistMenuActions

	data class DeletePlaylist(val playlist: Playlist): PlaylistMenuActions

}