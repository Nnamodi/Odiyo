@file:Suppress("UNCHECKED_CAST")

package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.OdiyoApp

@UnstableApi
class ViewModelFactory : ViewModelProvider.Factory {
	@RequiresApi(Build.VERSION_CODES.Q)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
			return MediaViewModel(
				appDataStore = OdiyoApp.appDataStore,
				musicRepository = OdiyoApp.musicRepository,
				mediaRepository = OdiyoApp.mediaRepository
			) as T
		}
		if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
			return NowPlayingViewModel(
				appDataStore = OdiyoApp.appDataStore,
				mediaRepository = OdiyoApp.mediaRepository,
				musicRepository = OdiyoApp.musicRepository
			) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}