package com.roland.android.odiyo.util

import android.net.Uri
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist

sealed interface QueueItemActions {

	data class Play(val item: QueueMediaItem) : QueueItemActions

	data class DuplicateSong(val item: QueueMediaItem): QueueItemActions

	data class RemoveSong(val item: QueueMediaItem): QueueItemActions

	data class CreatePlaylist(val playlist: Playlist): QueueItemActions

	data class AddToPlaylist(val songs: List<Music>, val playlist: Playlist): QueueItemActions

}

data class QueueMediaItem(
	val index: Int,
	val uri: Uri
)