package com.roland.android.odiyo.data

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaSource(
	private val scope: CoroutineScope,
	private val resolver: ContentResolver
) {
	private val query = resolver.query(
		MediaDetails.collection,
		MediaDetails.projection,
		null,
		null,
		MediaDetails.sortOrder
	)

	private val media = MutableStateFlow<List<Music>>(mutableListOf())

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
					val duration = cursor.getInt(durationColumn)

					val contentUri: Uri = ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						id
					)

					val thumbnail: Bitmap? =
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
							resolver.loadThumbnail(
								contentUri, Size(50, 50), null
							)
						} else { null }

					val music = Music(contentUri, name, title, artist, duration, thumbnail)
					media.value += music
				}
			}
		}
		return media
	}
}