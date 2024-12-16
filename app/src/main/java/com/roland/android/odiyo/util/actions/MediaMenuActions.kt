package com.roland.android.odiyo.util.actions

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.util.SortOptions

sealed interface MediaMenuActions {

	data class PlayAudio(
		val uri: Uri,
		val index: Int,
		val songsToPlay: List<Music>,
		val nowPlayingFrom: NowPlayingFrom
	) : MediaMenuActions

	data class PlayNext(
		val songs: List<Music>,
		val collectionType: String? = null,
		val collectionName: String? = null,
	) : MediaMenuActions

	data class AddToQueue(
		val songs: List<Music>,
		val collectionType: String? = null,
		val collectionName: String? = null,
	) : MediaMenuActions

	data class RenameSong(val details: SongDetails): MediaMenuActions

	data class Favorite(val song: Music): MediaMenuActions

	data class CreatePlaylist(val playlist: Playlist): MediaMenuActions

	data class AddToPlaylist(val songs: List<Music>, val playlist: Playlist): MediaMenuActions

	data class RemoveFromPlaylist(val songs: List<Music>, val playlist: Playlist): MediaMenuActions

	data class SetAsRingtone(val music: Music, val ringType: Int): MediaMenuActions

	data class ShareSong(val songs: List<Music>): MediaMenuActions

	data class SortSongs(val sortOptions: SortOptions): MediaMenuActions

	data class DeleteSongs(val songs: List<Music>): MediaMenuActions

}

data class SongDetails(
	val id: Long,
	val uri: Uri,
	val title: String? = null,
	val artist: String? = null
)
