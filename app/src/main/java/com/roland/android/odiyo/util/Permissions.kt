package com.roland.android.odiyo.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {
	private fun Activity.permissionRequest(
		permission: String,
		permissionGranted: (Boolean) -> Unit,
		showRational: (String) -> Unit
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
			) -> showRational(permission)

			else -> showRational(permission)
		}
	}

	fun Activity.storagePermission(
		permission: (String) -> Unit,
		permissionGranted: (Boolean) -> Unit,
	) {
		permissionRequest(
			permission = Manifest.permission.READ_EXTERNAL_STORAGE,
			permissionGranted = permissionGranted,
			showRational = permission
		)
	}
}