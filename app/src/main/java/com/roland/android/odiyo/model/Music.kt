package com.roland.android.odiyo.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.roland.android.odiyo.service.Util.time

data class Music(
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val time: Long,
	val thumbnail: Any?
) {
	@RequiresApi(Build.VERSION_CODES.Q)
	val duration = time.time
}
