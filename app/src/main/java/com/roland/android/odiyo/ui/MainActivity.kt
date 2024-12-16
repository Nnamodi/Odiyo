package com.roland.android.odiyo.ui

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roland.android.domain.util.IntentOptions
import com.roland.android.odiyo.R
import com.roland.android.odiyo.R.string.read_storage_rationale
import com.roland.android.odiyo.R.string.read_storage_request
import com.roland.android.odiyo.data.readStoragePermissionGranted
import com.roland.android.odiyo.ui.dialog.AudioIntentDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.navigation.AppRoute
import com.roland.android.odiyo.ui.navigation.NavActions
import com.roland.android.odiyo.ui.screens.media.MediaViewModel
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingViewModel
import com.roland.android.odiyo.ui.screens.settings.SettingsViewModel
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.Permissions.launchDeviceSettingsUi
import com.roland.android.odiyo.util.Permissions.readStoragePermission
import com.roland.android.odiyo.util.Permissions.rememberPermissionLauncher
import com.roland.android.odiyo.util.Permissions.storagePermissionPermanentlyDenied
import com.roland.android.odiyo.util.actions.AudioIntentActions
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
	private lateinit var audioIntent: MutableState<Uri?>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		volumeControlStream = AudioManager.STREAM_MUSIC
		audioIntent = mutableStateOf(intent.data)

		setDecorFitsSystemWindows(window, false)
		setTheme(R.style.Theme_Odiyo)

		setContent {
			val mediaViewModel: MediaViewModel = koinViewModel()
			val nowPlayingViewModel: NowPlayingViewModel = koinViewModel()
			val settingsViewModel: SettingsViewModel = koinViewModel()
			val navController = rememberNavController()
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
					AppRoute(navActions, navController)

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

					val uiState by remember(nowPlayingViewModel.nowPlayingUiState) {
						mutableStateOf(nowPlayingViewModel.nowPlayingUiState)
					}
					if ((audioIntent.value != null) && mediaViewModel.songsFetched) {
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
									mediaViewModel.audioIntentAction(
										AudioIntentActions.Play(audioIntent.value!!)
									)
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

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		val newData = intent.data
		audioIntent.value = newData
		Log.d(/* tag = */ "AudioIntentInfo", /* msg = */ "$newData")
	}

	companion object {
		fun newInstance(context: Context): Intent {
			return Intent(context, MainActivity::class.java)
		}
	}
}