package com.roland.android.odiyo.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.roland.android.odiyo.service.Util.date
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMb

@RequiresApi(Build.VERSION_CODES.Q)
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
	var favorite: Boolean = false
) {
	fun duration(): String = time.time
	fun size(): String = "${bytes.toMb} MB"
	fun dateAdded(): String = addedOn.date
}
