package com.roland.android.odiyo.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {
	fun Activity.permissionRequest(
		requestPermissionLauncher: ActivityResultLauncher<String>,
		permissionGranted: (Boolean) -> Unit,
	) {
		when {
			ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.READ_EXTERNAL_STORAGE
			) == PackageManager.PERMISSION_GRANTED -> {
				permissionGranted(true)
			}

			ActivityCompat.shouldShowRequestPermissionRationale(
				this,
				Manifest.permission.READ_EXTERNAL_STORAGE
			) -> {
				requestPermissionLauncher.launch(
					Manifest.permission.READ_EXTERNAL_STORAGE
				)
			}

			else -> {
				requestPermissionLauncher.launch(
					Manifest.permission.READ_EXTERNAL_STORAGE
				)
			}
		}
	}
}