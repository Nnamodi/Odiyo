package com.roland.android.odiyo.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class Converter {
	@TypeConverter
	fun fromUri(uri: Uri): String = uri.toString()

	@TypeConverter
	fun toUri(string: String): Uri = string.toUri()
}