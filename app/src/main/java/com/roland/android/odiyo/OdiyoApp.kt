package com.roland.android.odiyo

import android.app.Application
import com.roland.android.odiyo.repository.MediaRepository

class OdiyoApp : Application() {

	override fun onCreate() {
		super.onCreate()
		mediaRepository = MediaRepository(contentResolver)
	}

	companion object {
		lateinit var mediaRepository: MediaRepository
	}
}