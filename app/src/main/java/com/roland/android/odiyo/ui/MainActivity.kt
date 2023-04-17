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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.permissionRequest
import com.roland.android.odiyo.viewmodel.OdiyoViewModel
import com.roland.android.odiyo.viewmodel.ViewModelFactory

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MainActivity : ComponentActivity() {
	private lateinit var player: ExoPlayer
	private lateinit var mediaSession: MediaSession
//	private lateinit var mediaController: MediaController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val requestPermissionLauncher = registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			Log.i("MainActivity", "Permission granted: $isGranted")
		}
		player = ExoPlayer.Builder(this).build()
		mediaSession = MediaSession.Builder(this, player).build()
//		mediaController = MediaController.Builder(this, mediaSession.token)

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
					var currentSongUri by remember { mutableStateOf("null".toUri()) }
					if (permissionGranted) {
						val viewModel: OdiyoViewModel = viewModel(factory = ViewModelFactory())

						MediaScreen(
							songs = viewModel.songs,
							playAudio = { uri ->
								val sameSong = currentSongUri == uri
								mediaSession.player.apply {
									if (!sameSong) {
										stop(); removeMediaItem(0)
										addMediaItem(
											MediaItem.Builder().setUri(uri).build()
										)
										prepare(); play()
									}
									if (isPlaying) {
										if (!sameSong) { prepare(); play() } else pause()
									}
									player.pauseAtEndOfMediaItems = true
								}
								currentSongUri = uri
							},
							currentSongUri = currentSongUri
						)
					}
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		player.release()
		mediaSession.release()
	}
}