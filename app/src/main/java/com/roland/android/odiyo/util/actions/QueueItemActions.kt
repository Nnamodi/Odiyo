package com.roland.android.odiyo.util.actions

import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.model.QueueMediaItem

sealed interface QueueItemActions {

	data class Play(val item: QueueMediaItem) : QueueItemActions

	data class DuplicateSong(val item: QueueMediaItem): QueueItemActions

	data class RemoveSong(val item: QueueMediaItem): QueueItemActions

	data class CreatePlaylist(val playlist: Playlist): QueueItemActions

	data class AddToPlaylist(val songs: List<Music>, val playlist: Playlist): QueueItemActions

}