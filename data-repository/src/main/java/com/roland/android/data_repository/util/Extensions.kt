package com.roland.android.data_repository.util

import com.roland.android.data_repository.util.Constants.DATE
import com.roland.android.data_repository.util.Constants.MB_DIVISOR
import com.roland.android.data_repository.util.Constants.MB_FORMAT
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Extensions {

	val Long.time: String
		get() {
			val hour = ((this / 1000) / 60) / 60
			val minute = ((this / 1000) / 60) % 60
			val second = (this / 1000) % 60
			val hours = if (hour > 0) "$hour:" else ""
			val minutes = if (minute < 10) "0$minute:" else "$minute:"
			val seconds = if (second < 10) "0$second" else "$second"
			return "$hours$minutes$seconds"
		}

	val Long.date: String
		get() = SimpleDateFormat(DATE, Locale.getDefault()).format(this * 1000)

	val Int.toMb: String
		get() = DecimalFormat(MB_FORMAT).format(this / MB_DIVISOR)

}