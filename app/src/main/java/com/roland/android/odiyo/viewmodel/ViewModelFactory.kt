@file:Suppress("UNCHECKED_CAST")

package com.roland.android.odiyo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.OdiyoApp

class ViewModelFactory : ViewModelProvider.Factory {
	@RequiresApi(Build.VERSION_CODES.Q)
	@UnstableApi
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(OdiyoViewModel::class.java)) {
			return OdiyoViewModel(OdiyoApp.resolver) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}