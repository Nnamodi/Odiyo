package com.roland.android.odiyo.util

import com.roland.android.odiyo.model.Playlist

sealed interface PlaylistMenuActions {
	data class CreatePlaylist(val playlist: Playlist) : PlaylistMenuActions
	data class PlayNext(val songs: Playlist) : PlaylistMenuActions
	data class AddToQueue(val songs: Playlist) : PlaylistMenuActions
	data class RenamePlaylist(val playlist: Playlist): PlaylistMenuActions
	data class DeletePlaylist(val playlist: Playlist): PlaylistMenuActions
}