package com.roland.android.odiyo.mediaSource

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.util.SongDetails
import java.io.File
import java.io.FileOutputStream

class MediaAccessingObject(private val resolver: ContentResolver) {
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

	fun setAsRingtone(music: Music) {
		val ringtonePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
		val file = File(ringtonePath, music.name)
		val values = ContentValues().apply {
			put(MediaStore.MediaColumns.DATA, file.absolutePath)
			put(MediaStore.MediaColumns.DISPLAY_NAME, music.name)
			put(MediaStore.MediaColumns.TITLE, music.title)
			put(MediaStore.MediaColumns.SIZE, music.bytes)
			put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
			put(MediaStore.Audio.Media.ARTIST, music.artist)
			put(MediaStore.Audio.Media.ALBUM, music.album)
			put(MediaStore.Audio.Media.DURATION, music.time)
			put(MediaStore.Audio.Media.IS_RINGTONE, true)
			put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
			put(MediaStore.Audio.Media.IS_ALARM, false)
			put(MediaStore.Audio.Media.IS_MUSIC, false)
		}

		val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)!!
		val newRingtoneUri = resolver.insert(uri, values)
		var fileOutputStream: FileOutputStream? = null
		try {
			fileOutputStream = FileOutputStream(file)
			newRingtoneUri?.let {
				resolver.openFileDescriptor(it, "w")?.use { descriptor ->
					fileOutputStream.write(descriptor.describeContents())
				}
			}
//			newRingtoneUri?.describeContents()?.let { fileOutputStream.write(it) }
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close()
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}

//		resolver.update(
//			music.uri,
//			values,
//			mediaSelection,
//			arrayOf(music.id.toString())
//		)
	}
}