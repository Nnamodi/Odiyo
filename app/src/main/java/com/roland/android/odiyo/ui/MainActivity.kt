package com.roland.android.odiyo.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.roland.android.odiyo.service.Util.notificationManager
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.pendingIntent
import com.roland.android.odiyo.service.Util.storagePermissionGranted
import com.roland.android.odiyo.ui.dialog.AudioIntentDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.navigation.AppRoute
import com.roland.android.odiyo.ui.navigation.NavActions
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.storagePermission
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.NowPlayingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
	private lateinit var audioIntent: MutableState<Uri?>

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
		audioIntent = mutableStateOf(intent.data)

		val requestPermissionLauncher = registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			storagePermissionGranted.value = isGranted
			Log.i("PermissionInfo", "Permission granted: $isGranted")
		}

		setContent {
			val context = LocalContext.current
			val mediaViewModel: MediaViewModel = hiltViewModel()
			val nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
			val navController = rememberAnimatedNavController()
			val openPermissionDialog = remember { mutableStateOf(!mediaViewModel.canAccessStorage) }
			var permission by remember { mutableStateOf("") }
			val navActions = NavActions(
				navController = navController,
				storagePermissionGranted = mediaViewModel.canAccessStorage,
				requestPermission = { openPermissionDialog.value = true }
			)

			storagePermission(permission = { permission = it }) { isGranted ->
				openPermissionDialog.value = !isGranted
				storagePermissionGranted.value = isGranted
				if (!isGranted) audioIntent.value = null
				Log.d("PermissionInfo", "Storage permission granted: $isGranted")
			}

			OdiyoTheme(true) {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					AppRoute(
						navActions = navActions,
						navController = navController,
						mediaViewModel = mediaViewModel,
						nowPlayingViewModel = nowPlayingViewModel,
						playlistViewModel = hiltViewModel()
					)

					if (openPermissionDialog.value) {
						PermissionDialog(
							permissionMessage = stringResource(R.string.storage_permission_message),
							requestPermission = { requestPermissionLauncher.launch(permission) },
							openDialog = { openPermissionDialog.value = it }
						)
					}

					if (audioIntent.value != null) {
						AudioIntentDialog(
							uri = audioIntent.value!!,
							intentAction = { mediaViewModel.audioIntentAction(it); audioIntent.value = null },
							openDialog = { audioIntent.value = null }
						)
					}

					LaunchedEffect(true) {
						nowPlaying.collectLatest {
							val mediaArt: Bitmap = it?.getBitmap(context) ?:
							BitmapFactory.decodeResource(context.resources, R.drawable.default_art)

							mediaViewModel.currentMediaItemImage = mediaArt
							nowPlayingViewModel.currentMediaItemImage = mediaArt
						}
					}
				}

				val systemUiController = rememberSystemUiController()
				val useDarkIcons = isSystemInDarkTheme()
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

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		val newData = intent?.data
		audioIntent.value = newData
		Log.d(/* tag = */ "AudioIntentInfo", /* msg = */ "$newData")
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