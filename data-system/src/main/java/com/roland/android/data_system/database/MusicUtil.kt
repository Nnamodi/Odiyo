package com.roland.android.data_system.database

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.MediaStore
import com.roland.android.data_system.model.SongDetails
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicUtil : KoinComponent {
	private val resolver by inject<ContentResolver>()
	private val mediaSelection = "${MediaStore.Audio.Media._ID} = ?"

	fun updateSong(song: SongDetails) {
		val updatedSong = ContentValues().apply {
			song.title?.let { put(MediaStore.Audio.Media.TITLE, it) }
			song.artist?.let { put(MediaStore.Audio.Media.ARTIST, it) }
		}
		resolver.update(
			song.uri,
			updatedSong,
			mediaSelection,
			arrayOf(song.id.toString())
		)
	}

	fun deleteSong(song: SongDetails) {
		resolver.delete(
			song.uri,
			mediaSelection,
			arrayOf(song.id.toString())
		)
	}
}