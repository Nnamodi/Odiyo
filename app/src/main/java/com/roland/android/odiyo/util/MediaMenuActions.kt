package com.roland.android.odiyo.util

import android.net.Uri
import com.roland.android.odiyo.model.Music

sealed interface MediaMenuActions {
	data class PlayNext(val songs: List<Music>) : MediaMenuActions
	data class RenameSong(val details: SongDetails): MediaMenuActions
	data class ShareSong(val details: Music): MediaMenuActions
	data class DeleteSong(val details: SongDetails): MediaMenuActions
}

data class SongDetails(
	val id: Long,
	val uri: Uri,
	val title: String? = null,
	val artist: String? = null
)
