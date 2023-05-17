package com.roland.android.odiyo

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaAccessingObject
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.repository.MediaRepository

class OdiyoApp : Application() {
	private val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_preferences")

	@RequiresApi(Build.VERSION_CODES.Q)
	override fun onCreate() {
		super.onCreate()
		appDataStore = AppDataStore(datastore)
		mediaRepository = MediaRepository(
			mediaSource = MediaSource(contentResolver),
			albumsSource = AlbumsSource(contentResolver),
			artistsSource = ArtistsSource(contentResolver),
			mediaAccessingObject = MediaAccessingObject(contentResolver)
		)
	}

	companion object {
		lateinit var appDataStore: AppDataStore
		lateinit var mediaRepository: MediaRepository
	}
}