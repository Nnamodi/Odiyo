package com.roland.android.odiyo.ui

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roland.android.odiyo.R.string.*
import com.roland.android.odiyo.service.OdiyoNotificationManager
import com.roland.android.odiyo.service.PlayerListener
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.notificationManager
import com.roland.android.odiyo.service.Util.pendingIntent
import com.roland.android.odiyo.service.Util.readStoragePermissionGranted
import com.roland.android.odiyo.ui.dialog.AudioIntentDialog
import com.roland.android.odiyo.ui.dialog.IntentOptions
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.navigation.AppRoute
import com.roland.android.odiyo.ui.navigation.NavActions
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.AudioIntentActions
import com.roland.android.odiyo.util.Permissions.launchDeviceSettingsUi
import com.roland.android.odiyo.util.Permissions.readStoragePermission
import com.roland.android.odiyo.util.Permissions.rememberPermissionLauncher
import com.roland.android.odiyo.util.Permissions.storagePermissionPermanentlyDenied
import com.roland.android.odiyo.viewmodel.MediaViewModel
import com.roland.android.odiyo.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
	private lateinit var audioIntent: MutableState<Uri?>
	@Inject lateinit var player: ExoPlayer

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// mediaSession and notificationManager will be initialized and managed in the Service class for background media playback
		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(this.pendingIntent)
			.build()
		mediaSession?.player?.addListener(PlayerListener(this))
		notificationManager = OdiyoNotificationManager(this, mediaSession!!)
		notificationManager.showNotification(player)
		volumeControlStream = AudioManager.STREAM_MUSIC
		audioIntent = mutableStateOf(intent.data)

		WindowCompat.setDecorFitsSystemWindows(window, false)

		setContent {
			val mediaViewModel: MediaViewModel = hiltViewModel()
			val settingsViewModel: SettingsViewModel = hiltViewModel()
			val navController = rememberAnimatedNavController()
			val openPermissionDialog = remember { mutableStateOf(!mediaViewModel.canAccessStorage) }
			var permission by remember { mutableStateOf("") }
			val isDarkTheme = settingsViewModel.isDarkTheme ?: isSystemInDarkTheme()
			val navActions = NavActions(
				navController = navController,
				storagePermissionGranted = mediaViewModel.canAccessStorage,
				requestPermission = { openPermissionDialog.value = true }
			)

			val requestPermissionLauncher = rememberPermissionLauncher {
				readStoragePermissionGranted.value = it
				if (!it) mediaViewModel.savePermissionStatus(
					!shouldShowRequestPermissionRationale(this, permission)
				)
			}
			readStoragePermission(permission = { permission = it }) { isGranted ->
				openPermissionDialog.value = !isGranted
				readStoragePermissionGranted.value = isGranted
				if (!isGranted) audioIntent.value = null
				Log.d("PermissionInfo", "Storage permission granted: $isGranted")
			}

			OdiyoTheme(isDarkTheme) {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					AppRoute(
						navActions, navController, mediaViewModel,
						nowPlayingViewModel = hiltViewModel(),
						playlistViewModel = hiltViewModel(),
						settingsViewModel = settingsViewModel
					)

					if (openPermissionDialog.value) {
						PermissionDialog(
							permissionMessage = stringResource(
								if (storagePermissionPermanentlyDenied) read_storage_request else read_storage_rationale
							),
							requestPermission = {
								if (storagePermissionPermanentlyDenied) {
									launchDeviceSettingsUi(ACTION_APPLICATION_DETAILS_SETTINGS)
								} else requestPermissionLauncher.launch(permission)
							},
							openDialog = { openPermissionDialog.value = it }
						)
					}

					val uiState by remember(mediaViewModel.nowPlayingScreenUiState) {
						mutableStateOf(mediaViewModel.nowPlayingScreenUiState)
					}
					if ((audioIntent.value != null) && mediaViewModel.songsFetched &&
						(mediaViewModel.currentMediaItems.size == uiState.musicQueue.size)) {
						val audioIntentAction: (AudioIntentActions) -> Unit = {
							mediaViewModel.audioIntentAction(it)
							audioIntent.value = null
						}
						when (settingsViewModel.musicIntentOption) {
							IntentOptions.Play -> audioIntentAction(AudioIntentActions.Play(audioIntent.value!!))
							IntentOptions.PlayNext -> audioIntentAction(AudioIntentActions.PlayNext(audioIntent.value!!))
							IntentOptions.AddToQueue -> audioIntentAction(AudioIntentActions.AddToQueue(audioIntent.value!!))
							IntentOptions.AlwaysAsk -> {
								if (uiState.musicQueue.isNotEmpty()) {
									AudioIntentDialog(
										uri = audioIntent.value!!,
										intentAction = audioIntentAction,
										openDialog = { audioIntent.value = null }
									)
								} else {
									mediaViewModel.playAudioFromIntent(audioIntent.value!!)
									audioIntent.value = null
								}
							}
						}
					}
				}

				val systemUiController = rememberSystemUiController()

				SideEffect {
					systemUiController.setSystemBarsColor(
						color = Color.Transparent,
						darkIcons = !isDarkTheme
					)
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		val viewModel: MediaViewModel = viewModels<MediaViewModel>().value
		readStoragePermission(permission = {}) { isGranted ->
			readStoragePermissionGranted.value = isGranted
			if (isGranted) viewModel.savePermissionStatus(permanentlyDenied = false)
			Log.d("PermissionInfo", "Storage permission granted: $isGranted")
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
			player.apply { removeListener(PlayerListener(this@MainActivity)); release() }
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