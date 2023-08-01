package com.roland.android.odiyo.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.roland.android.odiyo.R

object Permissions {
	var storagePermissionPermanentlyDenied = false

	private fun Activity.permissionRequest(
		permission: String,
		permissionGranted: (Boolean) -> Unit,
		showRational: (String) -> Unit
	) {
		when (PackageManager.PERMISSION_GRANTED) {
			ContextCompat.checkSelfPermission(
				this, permission
			) -> permissionGranted(true)
			else -> {
				showRational(permission)
				permissionGranted(false)
			}
		}
	}

	fun Activity.readStoragePermission(
		permission: (String) -> Unit,
		permissionGranted: (Boolean) -> Unit,
	) {
		permissionRequest(
			permission = Manifest.permission.READ_EXTERNAL_STORAGE,
			permissionGranted = permissionGranted,
			showRational = permission
		)
	}

	fun Context.writeStoragePermission(
		permission: (String) -> Unit,
		permissionGranted: (Boolean) -> Unit,
	) {
		(this as Activity).permissionRequest(
			permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
			permissionGranted = permissionGranted,
			showRational = permission
		)
	}

	fun Context.launchDeviceSettingsUi(action: String) {
		val intent = Intent(action)
		intent.data = Uri.parse("package:$packageName")
		startActivity(intent)
	}

	@Composable
	fun rememberPermissionLauncher(onResult: (Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
		val context = LocalContext.current
		return rememberLauncherForActivityResult(
			contract = ActivityResultContracts.RequestPermission(),
			onResult = {
				onResult(it)
				if (it) Toast.makeText(context, context.getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
				Log.i("PermissionInfo", "Permission granted: $it")
			}
		)
	}
}