package com.roland.android.odiyo.mediaSource

import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object MediaDetails {
	// query parameters for all songs
	val libraryCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Media.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		}

	val libraryProjection = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.DISPLAY_NAME,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.DURATION,
		MediaStore.Audio.Media.SIZE,
		MediaStore.Audio.Media.DATE_ADDED,
		MediaStore.Audio.Media.ALBUM
	)

	const val librarySortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

	// query parameters for albums
	val albumCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Albums.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
		}

	val albumProjection = arrayOf(
		MediaStore.Audio.Albums._ID,
		MediaStore.Audio.Albums.NUMBER_OF_SONGS,
		MediaStore.Audio.Albums.ALBUM
	)

	const val albumSelection = "${MediaStore.Audio.Media.ALBUM} == ?"

	const val albumSortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

	// query parameters for artists
	val artistCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Artists.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
		}

	val artistProjection = arrayOf(
		MediaStore.Audio.Artists._ID,
		MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
		MediaStore.Audio.Artists.ARTIST
	)

	const val artistSelection = "${MediaStore.Audio.Media.ARTIST} == ?"

	const val artistSortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"
}