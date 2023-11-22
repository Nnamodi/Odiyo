package com.roland.android.odiyo.util

import android.net.Uri

sealed interface AudioIntentActions {

	data class Play(val uri: Uri): AudioIntentActions

	data class PlayNext(val uri: Uri): AudioIntentActions

	data class AddToQueue(val uri: Uri): AudioIntentActions

}