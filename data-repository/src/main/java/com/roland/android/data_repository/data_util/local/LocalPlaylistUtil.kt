package com.roland.android.data_repository.data_util.local

import com.roland.android.domain.model.Playlist

interface LocalPlaylistUtil {

	fun createPlaylist(playlist: Playlist)

	fun updatePlaylist(playlist: Playlist)

	fun deletePlaylist(playlist: Playlist)

}