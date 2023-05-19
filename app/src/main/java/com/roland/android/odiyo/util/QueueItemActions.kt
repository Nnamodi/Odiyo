package com.roland.android.odiyo.util

import android.net.Uri

sealed interface QueueItemActions {
	data class Play(val item: QueueMediaItem) : QueueItemActions
	data class RemoveSong(val item: QueueMediaItem): QueueItemActions
}

data class QueueMediaItem(
	val index: Int,
	val uri: Uri
)