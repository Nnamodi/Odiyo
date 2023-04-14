package com.roland.android.odiyo.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.permissionRequest

class MainActivity : ComponentActivity() {
	@RequiresApi(Build.VERSION_CODES.S)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val requestPermissionLauncher = registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			Log.i("MainActivity", "Permission granted: $isGranted")
		}

		setContent {
			var permissionGranted by rememberSaveable { mutableStateOf(false) }

			permissionRequest(requestPermissionLauncher) {
				permissionGranted = it
			}

			OdiyoTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					if (permissionGranted) { MediaScreen() }
				}
			}
		}
	}
}