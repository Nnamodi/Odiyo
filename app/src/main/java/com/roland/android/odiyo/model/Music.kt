package com.roland.android.odiyo.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.roland.android.odiyo.service.Util.date
import com.roland.android.odiyo.service.Util.time
import com.roland.android.odiyo.service.Util.toMb

@RequiresApi(Build.VERSION_CODES.Q)
data class Music(
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val time: Long,
	val thumbnail: Any?,
	val bytes: Int = 0,
	val addedOn: Long = 0,
	val album: String = "",
	val path: String = ""
) {
	val duration = time.time
	val size = "${bytes.toMb} MB"
	val dateAdded = addedOn.date
}
