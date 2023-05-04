package com.roland.android.odiyo.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.roland.android.odiyo.data.MediaDetails.albumSelection
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.use

class MediaSource(
	private val scope: CoroutineScope,
	private val resolver: ContentResolver
) {
	private val query = resolver.query(
		MediaDetails.libraryCollection,
		MediaDetails.libraryProjection,
		null,
		null,
		MediaDetails.librarySortOrder
	)

	private val albumMediaQuery: (Array<String>) -> Cursor? = {
		resolver.query(
			MediaDetails.libraryCollection,
			MediaDetails.libraryProjection,
			albumSelection,
			it,
			MediaDetails.librarySortOrder
		)
	}

	private val media = MutableStateFlow<List<Music>>(mutableListOf())

	private val mediaFromAlbum = MutableStateFlow<List<Music>>(mutableListOf())

	fun media(): MutableStateFlow<List<Music>> {
		scope.launch {
			query?.use { cursor ->
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
				val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
				val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
				val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val name = cursor.getString(nameColumn)
					val title = cursor.getString(titleColumn)
					val artist = cursor.getString(artistColumn)
					val duration = cursor.getLong(durationColumn)

					val contentUri: Uri = ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						id
					)

					// val thumbnail: Bitmap? = contentUri.toBitmap(resolver)

					val music = Music(contentUri, name, title, artist, duration, null)
					media.value += music
				}
			}
		}
		return media
	}

	fun mediaFromAlbum(
		selectionArgs: Array<String>
	): MutableStateFlow<List<Music>> {
		// empty list of songs previously fetched and re-fetch
		mediaFromAlbum.value = mutableListOf()
		scope.launch {
			albumMediaQuery(selectionArgs)?.use { cursor ->
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
				val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
				val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
				val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val name = cursor.getString(nameColumn)
					val title = cursor.getString(titleColumn)
					val artist = cursor.getString(artistColumn)
					val duration = cursor.getLong(durationColumn)

					val contentUri: Uri = ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						id
					)

					// val thumbnail: Bitmap? = contentUri.toBitmap(resolver)

					val music = Music(contentUri, name, title, artist, duration, null)
					mediaFromAlbum.value += music
				}
			}
		}
		return mediaFromAlbum
	}
}