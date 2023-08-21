package com.roland.android.odiyo.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.roland.android.odiyo.service.Util.date
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMb
import java.util.*

@Entity
data class Music(
	val id: Long,
	val uri: Uri,
	val name: String,
	var title: String,
	var artist: String,
	val time: Long,
	val bytes: Int = 0,
	val addedOn: Long = 0,
	val album: String = "",
	val path: String = "",

	@PrimaryKey(autoGenerate = true)
	val generatedId: Int = 0,
	var favorite: Boolean = false,
	var lastPlayed: Date = Date(0),
	var timesPlayed: Int? = 0
) {
	fun duration(): String = time.time
	fun size(): String = "${bytes.toMb} MB"
	fun dateAdded(): String = addedOn.date
}

data class MusicFromSystem(
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val time: Long,
	val bytes: Int = 0,
	val addedOn: Long = 0,
	val album: String = "",
	val path: String = ""
)