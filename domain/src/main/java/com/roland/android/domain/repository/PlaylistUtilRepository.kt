package com.roland.android.domain.repository

import com.roland.android.domain.model.Playlist

interface PlaylistUtilRepository {

	fun createPlaylist(playlist: Playlist)

	fun updatePlaylist(playlist: Playlist)

	fun deletePlaylist(playlist: Playlist)

}