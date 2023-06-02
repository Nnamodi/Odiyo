package com.roland.android.odiyo.mediaSource

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.roland.android.odiyo.mediaSource.MediaDetails.albumSelection
import com.roland.android.odiyo.mediaSource.MediaDetails.artistSelection
import com.roland.android.odiyo.model.MusicFromSystem
import kotlinx.coroutines.flow.MutableStateFlow
import okio.use

class MediaSource(
	private val resolver: ContentResolver
) {
	private val query = resolver.query(
		MediaDetails.libraryCollection,
		MediaDetails.libraryProjection,
		null,
		null,
		MediaDetails.librarySortOrder
	)

	private val collectionMediaQuery: (String, Array<String>) -> Cursor? = { selection, selectionArgs ->
		resolver.query(
			MediaDetails.libraryCollection,
			MediaDetails.libraryProjection,
			selection,
			selectionArgs,
			MediaDetails.librarySortOrder
		)
	}

	private val media = MutableStateFlow<MutableList<MusicFromSystem>>(mutableListOf())

	private val mediaFromCollection = MutableStateFlow<MutableList<MusicFromSystem>>(mutableListOf())

	fun media(): MutableStateFlow<MutableList<MusicFromSystem>> {
		query?.use { cursor ->
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
			val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
			val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
			val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
			val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
			val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
			val addedOnColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
			val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
			val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val name = cursor.getString(nameColumn)
				val title = cursor.getString(titleColumn)
				val artist = cursor.getString(artistColumn)
				val duration = cursor.getLong(durationColumn)
				val size = cursor.getInt(sizeColumn)
				val addedOn = cursor.getLong(addedOnColumn)
				val album = cursor.getString(albumColumn)
				val path = cursor.getString(pathColumn)

				val contentUri: Uri = ContentUris.withAppendedId(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					id
				)

				val music = MusicFromSystem(id, contentUri, name, title, artist, duration, size, addedOn, album, path)
				media.value += music
			}
			Log.i("DataInfo", "Just fetched: ${media.value.size}")
		}
		return media
	}

	private fun mediaFromCollection(
		selection: String,
		selectionArgs: Array<String>
	): MutableStateFlow<MutableList<MusicFromSystem>> {
		// empty list of songs previously fetched and re-fetch
		mediaFromCollection.value = mutableListOf()
		collectionMediaQuery(selection, selectionArgs)?.use { cursor ->
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
			val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
			val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
			val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
			val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
			val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
			val addedOnColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
			val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
			val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val name = cursor.getString(nameColumn)
				val title = cursor.getString(titleColumn)
				val artist = cursor.getString(artistColumn)
				val duration = cursor.getLong(durationColumn)
				val size = cursor.getInt(sizeColumn)
				val addedOn = cursor.getLong(addedOnColumn)
				val album = cursor.getString(albumColumn)
				val path = cursor.getString(pathColumn)

				val contentUri: Uri = ContentUris.withAppendedId(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					id
				)

				val music = MusicFromSystem(id, contentUri, name, title, artist, duration, size, addedOn, album, path)
				mediaFromCollection.value += music
			}
			Log.i("DataInfo", "Just fetched from collection: ${media.value.size}")
		}
		return mediaFromCollection
	}

	fun mediaFromAlbum(
		selectionArgs: Array<String>
	): MutableStateFlow<MutableList<MusicFromSystem>> {
		mediaFromCollection(albumSelection, selectionArgs)
		return mediaFromCollection
	}

	fun mediaFromArtist(
		selectionArgs: Array<String>
	): MutableStateFlow<MutableList<MusicFromSystem>> {
		mediaFromCollection(artistSelection, selectionArgs)
		return mediaFromCollection
	}
}