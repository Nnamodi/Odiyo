package com.roland.android.data_system.database

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.roland.android.data_repository.model.MusicFromSystem
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicSource : KoinComponent {
	private val resolver by inject<ContentResolver>()

	// query parameters for all songs
	private val musicCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Media.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		}
	private val musicProjection = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.DISPLAY_NAME,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.DURATION,
		MediaStore.Audio.Media.SIZE,
		MediaStore.Audio.Media.DATE_ADDED,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA
	)
	private val musicSortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

	private fun query(
		selection: String?,
		selectionArgs: Array<String>?
	): Cursor? = resolver.query(
		musicCollection,
		musicProjection,
		selection,
		selectionArgs,
		musicSortOrder
	)

	fun getSongs(
		selection: String? = null,
		selectionArgs: Array<String>? = null
	): MutableStateFlow<List<MusicFromSystem>> {
		val songs = MutableStateFlow<List<MusicFromSystem>>(mutableListOf())
		query(selection, selectionArgs)?.use { cursor ->
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
				songs.value += music
			}
			Log.i("DataInfo", "Just fetched: ${songs.value.size}")
		}
		return songs
	}
}