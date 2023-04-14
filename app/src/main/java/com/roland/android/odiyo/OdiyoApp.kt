package com.roland.android.odiyo

import android.app.Application
import android.content.ContentResolver

class OdiyoApp : Application() {

	override fun onCreate() {
		super.onCreate()
		resolver = contentResolver
	}

	companion object {
		lateinit var resolver: ContentResolver
	}
}