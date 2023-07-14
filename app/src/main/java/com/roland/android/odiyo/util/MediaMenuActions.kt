package com.roland.android.odiyo.util

import android.net.Uri
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.ui.dialog.SortOptions

sealed interface MediaMenuActions {
	data class PlayNext(val songs: List<Music>) : MediaMenuActions
	data class AddToQueue(val songs: List<Music>) : MediaMenuActions
	data class RenameSong(val details: SongDetails): MediaMenuActions
	data class Favorite(val song: Music): MediaMenuActions
	data class CreatePlaylist(val playlist: Playlist): MediaMenuActions
	data class AddToPlaylist(val songs: List<Music>, val playlist: Playlist): MediaMenuActions
	data class RemoveFromPlaylist(val songs: List<Music>, val playlistName: String): MediaMenuActions
	data class SetAsRingtone(val music: Music): MediaMenuActions
	data class ShareSong(val songs: List<Music>): MediaMenuActions
	data class SortSongs(val sortOptions: SortOptions): MediaMenuActions
	data class DeleteSongs(val songs: List<SongDetails>): MediaMenuActions
}

data class SongDetails(
	val id: Long,
	val uri: Uri,
	val title: String? = null,
	val artist: String? = null
)
