package com.roland.android.odiyo.ui

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.service.OdiyoNotificationManager
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.audioAttribute
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.permissionRequest
import com.roland.android.odiyo.viewmodel.OdiyoViewModel
import com.roland.android.odiyo.viewmodel.ViewModelFactory

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MainActivity : ComponentActivity() {
	private lateinit var notificationManager: OdiyoNotificationManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val player = ExoPlayer.Builder(this)
			.setAudioAttributes(audioAttribute, true)
			.build()
		mediaSession = MediaSession.Builder(this, player).build()
		notificationManager = OdiyoNotificationManager(context = this)
		notificationManager.showNotification(player)

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
					if (permissionGranted) {
						val viewModel: OdiyoViewModel = viewModel(factory = ViewModelFactory())
						player.apply { setMediaItems(viewModel.mediaItems); prepare() }

						MediaScreen(
							songs = viewModel.songs,
							playAudio = viewModel::playAudio,
							currentSongUri = viewModel.nowPlaying ?: NOTHING_PLAYING
						)
					}
				}
			}
		}
	}

	override fun onDestroy() {
		notificationManager.hideNotification()
		mediaSession?.apply {
			player.release()
			release()
			mediaSession = null
		}
		super.onDestroy()
	}

	companion object {
		fun newInstance(context: Context): Intent {
			return Intent(context, MainActivity::class.java)
		}
	}
}