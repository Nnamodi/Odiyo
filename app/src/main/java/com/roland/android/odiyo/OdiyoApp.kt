package com.roland.android.odiyo

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.repository.MediaRepository

class OdiyoApp : Application() {
	private val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_preferences")

	override fun onCreate() {
		super.onCreate()
		appDataStore = AppDataStore(datastore)
		mediaRepository = MediaRepository(contentResolver)
	}

	companion object {
		lateinit var appDataStore: AppDataStore
		lateinit var mediaRepository: MediaRepository
	}
}