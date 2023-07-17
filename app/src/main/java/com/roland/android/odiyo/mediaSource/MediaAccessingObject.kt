package com.roland.android.odiyo.mediaSource

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.provider.MediaStore
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.util.SongDetails

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

	fun setAsRingtone(context: Context, music: Music) {
		RingtoneManager.setActualDefaultRingtoneUri(
			context, RingtoneManager.TYPE_RINGTONE, music.uri
		)
	}
}