@file:Suppress("UNCHECKED_CAST")

package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.roland.android.odiyo.OdiyoApp

class ViewModelFactory : ViewModelProvider.Factory {
	@RequiresApi(Build.VERSION_CODES.Q)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
			return MediaViewModel(OdiyoApp.mediaRepository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}