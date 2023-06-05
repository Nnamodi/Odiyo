package com.roland.android.odiyo.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import java.util.*

class Converter {
	@TypeConverter
	fun fromUri(uri: Uri): String = uri.toString()

	@TypeConverter
	fun toUri(string: String): Uri = string.toUri()

	@TypeConverter
	fun fromDate(date: Date?): Long? = date?.time

	@TypeConverter
	fun toDate(milliSec: Long?): Date? = milliSec?.let { Date(it) }

	@TypeConverter
	fun fromListOfSongs(songs: List<Uri>): String {
		return songs.map { it.toString() }.joinToString { "|" }
	}

	@TypeConverter
	fun toListOfSongs(songs: String): List<Uri> {
		return songs.split("|").map { it.toUri() }
	}
}