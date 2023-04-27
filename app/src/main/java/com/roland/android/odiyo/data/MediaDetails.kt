package com.roland.android.odiyo.data

import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object MediaDetails {
	val collection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Media.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		}

	val projection = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.DISPLAY_NAME,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.DURATION
	)

//	val selection = "${MediaStore.Audio.Media.DISPLAY_NAME.endsWith("?")}"

//	val selectionArgs = arrayOf(
//		".mp3"
//	)

	const val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
}