package com.roland.android.odiyo.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {
	private fun Activity.permissionRequest(
		requestPermissionLauncher: ActivityResultLauncher<String>,
		permission: String,
		permissionGranted: (Boolean) -> Unit,
	) {
		when {
			ContextCompat.checkSelfPermission(
				this,
				permission
			) == PackageManager.PERMISSION_GRANTED -> {
				permissionGranted(true)
			}

			ActivityCompat.shouldShowRequestPermissionRationale(
				this,
				permission
			) -> {
				requestPermissionLauncher.launch(
					permission
				)
			}

			else -> {
				requestPermissionLauncher.launch(
					permission
				)
			}
		}
	}

	fun Activity.storagePermission(
		requestPermissionLauncher: ActivityResultLauncher<String>,
		permissionGranted: (Boolean) -> Unit,
	) {
		permissionRequest(
			requestPermissionLauncher,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			permissionGranted
		)
	}
}