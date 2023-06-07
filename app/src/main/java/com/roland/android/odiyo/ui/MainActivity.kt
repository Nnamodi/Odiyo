package com.roland.android.odiyo.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.OdiyoNotificationManager
import com.roland.android.odiyo.service.PlayerListener
import com.roland.android.odiyo.service.Util.audioAttribute
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.pendingIntent
import com.roland.android.odiyo.ui.navigation.AppRoute
import com.roland.android.odiyo.ui.navigation.NavActions
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.storagePermission
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel
import com.roland.android.odiyo.viewmodel.PlaylistViewModel
import com.roland.android.odiyo.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MainActivity : ComponentActivity() {
	private lateinit var notificationManager: OdiyoNotificationManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// player, mediaSession and notificationManager will be initialized and managed in the Service class for background media playback
		val player = ExoPlayer.Builder(this)
			.setAudioAttributes(audioAttribute, true)
			.build()
		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(this.pendingIntent)
			.build()
		mediaSession?.player?.addListener(PlayerListener())
		notificationManager = OdiyoNotificationManager(this, mediaSession!!)
		notificationManager.showNotification(player)
		volumeControlStream = AudioManager.STREAM_MUSIC

		val requestPermissionLauncher = registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			Log.i("MainActivity", "Permission granted: $isGranted")
		}

		setContent {
			var permissionGranted by rememberSaveable { mutableStateOf(false) }
			val context = LocalContext.current

			storagePermission(requestPermissionLauncher) {
				permissionGranted = it
			}

			OdiyoTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					if (permissionGranted) {
						val mediaViewModel: MediaViewModel = viewModel(factory = ViewModelFactory())
						val nowPlayingViewModel: NowPlayingViewModel = viewModel(factory = ViewModelFactory())
						val playlistViewModel: PlaylistViewModel = viewModel(factory = ViewModelFactory())
						val navController = rememberAnimatedNavController()
						val navActions = NavActions(navController)

						AppRoute(
							navActions = navActions,
							navController = navController,
							mediaViewModel = mediaViewModel,
							nowPlayingViewModel = nowPlayingViewModel,
							playlistViewModel = playlistViewModel
						)

						LaunchedEffect(true) {
							nowPlaying.collectLatest {
								val mediaArt: Bitmap = it?.getBitmap(context) ?:
								BitmapFactory.decodeResource(context.resources, R.drawable.default_art)

								mediaViewModel.currentMediaItemImage = mediaArt
								nowPlayingViewModel.currentMediaItemImage = mediaArt
							}
						}
					}
				}

				val systemUiController = rememberSystemUiController()
				val useDarkIcons = !isSystemInDarkTheme()
				val color = MaterialTheme.colorScheme.background

				DisposableEffect(systemUiController, useDarkIcons) {
					systemUiController.setSystemBarsColor(
						color = color,
						darkIcons = useDarkIcons
					)
					onDispose {}
				}
			}
		}
	}

	override fun onDestroy() {
		notificationManager.hideNotification()
		mediaSession?.apply {
			player.apply { removeListener(PlayerListener()); release() }
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