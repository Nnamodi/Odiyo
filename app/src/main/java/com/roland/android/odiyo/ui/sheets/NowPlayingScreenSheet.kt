package com.roland.android.odiyo.ui.sheets

import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.BottomSheetDefaults.ContainerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.dialog.RenameSongDialog
import com.roland.android.odiyo.ui.dialog.SetRingtoneDialog
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.sheets.MenuItems.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.dark_tertiaryContainer
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.Permissions.launchDeviceSettingsUi
import com.roland.android.odiyo.util.Permissions.rememberPermissionLauncher
import com.roland.android.odiyo.util.Permissions.writeStoragePermission
import com.roland.android.odiyo.util.SongDetails
import com.roland.android.odiyo.util.sheetHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreenSheet(
	currentSong: Music,
	scaffoldState: SheetState,
	componentColor: Color,
	containerColor: Color,
	openBottomSheet: (Boolean) -> Unit,
	openAddToPlaylistDialog: (List<Music>) -> Unit,
	menuAction: (MediaMenuActions) -> Unit
) {
	val containerColorBlend = ColorUtils.blendARGB(Color.White.toArgb(), containerColor.toArgb(), 0.95f)
	val customContainerColor = if (containerColor == ContainerColor) containerColor else Color(containerColorBlend)
	val openDetailsDialog = remember { mutableStateOf(false) }
	val openRenameDialog = remember { mutableStateOf(false) }
	val openRingtoneDialog = remember { mutableStateOf(false) }
	val openWriteSettingsUi = remember { mutableStateOf(false) }
	val openPermissionDialog = remember { mutableStateOf(false) }
	val writeStoragePermissionGranted = remember { mutableStateOf(false) }
	var permission by remember { mutableStateOf("") }
	val context = LocalContext.current
	val requestPermissionLauncher = rememberPermissionLauncher(
		onResult = { writeStoragePermissionGranted.value = it }
	)

	context.writeStoragePermission({ permission = it }) { isGranted ->
		writeStoragePermissionGranted.value = isGranted
		Log.d("PermissionInfo", "Storage write permission granted: $isGranted")
	}

	ModalBottomSheet(
		modifier = Modifier.absoluteOffset(y = 16.dp),
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
		containerColor = customContainerColor,
		dragHandle = { BottomSheetDefaults.DragHandle(color = componentColor) }
	) {
		Column(
			modifier = Modifier
				.heightIn(min = 10.dp, max = (sheetHeight().dp + 50.dp))
				.verticalScroll(rememberScrollState())
		) {
			val sheetMenuItems = MenuItems.values()
				.filter { menu ->
					arrayOf(Rename, AddToPlaylist, SetAsRingtone, Share, Details).any { it == menu }
				}

			sheetMenuItems.forEach { menu ->
				val action = { when (menu) {
					AddToPlaylist -> { openAddToPlaylistDialog(listOf(currentSong)); openBottomSheet(false) }
					SetAsRingtone -> {
						if (Settings.System.canWrite(context)) {
							openRingtoneDialog.value = true
						} else { openWriteSettingsUi.value = true }
					}
					Rename -> {
						openPermissionDialog.value = !writeStoragePermissionGranted.value
						openRenameDialog.value = writeStoragePermissionGranted.value
					}
					Share -> menuAction(MediaMenuActions.ShareSong(listOf(currentSong)))
					Details -> openDetailsDialog.value = true
					else -> {}
				} }
				SheetItem(
					icon = menu.icon, menuText = stringResource(menu.menuText),
					componentColor = componentColor, backgroundColor = customContainerColor
				) { action() }
			}
		}
	}

	if (openRenameDialog.value) {
		RenameSongDialog(
			song = currentSong,
			renameSong = { title, artist ->
				menuAction(
					MediaMenuActions.RenameSong(
						SongDetails(currentSong.id, currentSong.uri, title, artist)
					)
				)
				openBottomSheet(false)
			},
			openDialog = { openRenameDialog.value = it }
		)
	}

	if (openRingtoneDialog.value) {
		SetRingtoneDialog(
			song = currentSong,
			onRingtoneSet = { menuAction(it); openBottomSheet(false) },
			openDialog = { openRingtoneDialog.value = false }
		)
	}

	if (openDetailsDialog.value) {
		SongDetailsDialog(currentSong) { openDetailsDialog.value = it }
	}

	if (openPermissionDialog.value || openWriteSettingsUi.value) {
		PermissionDialog(
			icon = if (openWriteSettingsUi.value) Icons.Rounded.AddAlert else Icons.Rounded.MusicNote,
			iconColor = if (openWriteSettingsUi.value) dark_tertiaryContainer else Color.Blue,
			permissionMessage = stringResource(
				if (openWriteSettingsUi.value) {
					R.string.write_settings_permission
				} else R.string.write_storage_permission_message
			),
			requestPermission = {
				if (openWriteSettingsUi.value) {
					context.launchDeviceSettingsUi(ACTION_MANAGE_WRITE_SETTINGS); openWriteSettingsUi.value = false
				} else { requestPermissionLauncher.launch(permission) }
			},
			openDialog = { openPermissionDialog.value = it; openWriteSettingsUi.value = it }
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NowPlayingScreenSheetPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState(true)
		val openBottomSheet = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.clickable { openBottomSheet.value = true }
				.fillMaxSize()
		) {
			if (openBottomSheet.value) {
				NowPlayingScreenSheet(
					currentSong = previewData[5],
					scaffoldState = sheetState,
					componentColor = MaterialTheme.colorScheme.onBackground,
					containerColor = ContainerColor,
					openBottomSheet = { openBottomSheet.value = it },
					openAddToPlaylistDialog = {}
				) {}
			}
		}
	}
}