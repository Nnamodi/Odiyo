package com.roland.android.data_repository.data_util.player

import android.net.Uri
import com.roland.android.domain.model.CurrentPlaylist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem

interface MusicQueueUtil {

	fun restorePlaylistDetails(playlistDetails: CurrentPlaylist)

	fun playNext(uri: Uri)

	fun playNext(songs: List<Music>)

	fun addToQueue(uri: Uri)

	fun addToQueue(songs: List<Music>)

	fun playFromQueue(song: QueueMediaItem)

	fun duplicateSong(song: QueueMediaItem)

	fun removeSong(song: QueueMediaItem)

}