package com.roland.android.odiyo.ui.sheets

import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.PermissionDialog
import com.roland.android.odiyo.ui.dialog.RenameSongDialog
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.sheets.MenuItems.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.dark_tertiaryContainer
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.Permissions.launchWriteSettingsUi
import com.roland.android.odiyo.util.Permissions.writeStoragePermission
import com.roland.android.odiyo.util.SongDetails
import com.roland.android.odiyo.util.sheetHeight

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaItemSheet(
	song: Music,
	scaffoldState: SheetState,
	collectionIsPlaylist: Boolean = false,
	goToCollection: (String, String) -> Unit,
	openBottomSheet: (Boolean) -> Unit,
	openAddToPlaylistDialog: (List<Music>) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	removeFromPlaylist: (Music) -> Unit = {}
) {
	val openRenameDialog = remember { mutableStateOf(false) }
	val openDetailsDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }
	val openWriteSettingsUi = remember { mutableStateOf(false) }
	val openPermissionDialog = remember { mutableStateOf(false) }
	val writeStoragePermissionGranted = remember { mutableStateOf(false) }
	var permission by remember { mutableStateOf("") }
	val context = LocalContext.current
	val requestPermissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { writeStoragePermissionGranted.value = it }
	)

	context.writeStoragePermission({ permission = it }) { isGranted ->
		writeStoragePermissionGranted.value = isGranted
		Log.d("PermissionInfo", "Storage write permission granted: $isGranted")
	}

	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
		dragHandle = {
			MediaItem(
				modifier = Modifier
					.padding(10.dp)
					.clip(BottomSheetDefaults.ExpandedShape)
					.clickable {},
				song = song,
				currentMediaItem = Util.NOTHING_PLAYING,
				inSelectionMode = false,
				selected = false,
				showTrailingIcon = false,
				openMenuSheet = {}
			)
		}
	) {
		val menuItems = MenuItems.values()

		Column(
			modifier = Modifier
				.height(sheetHeight().dp)
				.verticalScroll(rememberScrollState())
		) {
			menuItems.forEach { menu ->
				val action = { when (menu) {
					PlayNext -> { menuAction(MediaMenuActions.PlayNext(listOf(song))); openBottomSheet(false) }
					AddToQueue -> { menuAction(MediaMenuActions.AddToQueue(listOf(song))); openBottomSheet(false) }
					Rename -> {
						openPermissionDialog.value = !writeStoragePermissionGranted.value
						openRenameDialog.value = writeStoragePermissionGranted.value
					}
					AddToFavorite -> { menuAction(MediaMenuActions.Favorite(song)); openBottomSheet(false) }
					AddToPlaylist -> { openAddToPlaylistDialog(listOf(song)) }
					SetAsRingtone -> {
						if (Settings.System.canWrite(context)) {
							menuAction(MediaMenuActions.SetAsRingtone(song)); openBottomSheet(false)
						} else { openWriteSettingsUi.value = true }
					}
					Share -> menuAction(MediaMenuActions.ShareSong(listOf(song)))
					GoToAlbum -> { goToCollection(song.album, ALBUMS); openBottomSheet(false) }
					GoToArtist -> { goToCollection(song.artist, ARTISTS); openBottomSheet(false) }
					Details -> openDetailsDialog.value = true
					Delete -> if (collectionIsPlaylist) {
						removeFromPlaylist(song); openBottomSheet(false)
					} else {
						openPermissionDialog.value = !writeStoragePermissionGranted.value
						openDeleteDialog.value = writeStoragePermissionGranted.value
					}
				} }
				val text = when (menu) {
					AddToFavorite -> if (song.favorite) R.string.remove_from_favorite else menu.menuText
					Delete -> if (collectionIsPlaylist) R.string.remove else menu.menuText
					else -> { menu.menuText }
				}
				SheetItem(menu.icon, stringResource(text)) { action() }
			}
		}
	}

	if (openRenameDialog.value) {
		RenameSongDialog(
			song = song,
			renameSong = { title, artist ->
				menuAction(
					MediaMenuActions.RenameSong(
						SongDetails(song.id, song.uri, title, artist)
					)
				)
				openBottomSheet(false)
			},
			openDialog = { openRenameDialog.value = it }
		)
	}

	if (openDetailsDialog.value) {
		SongDetailsDialog(song) { openDetailsDialog.value = it }
	}

	if (openDeleteDialog.value) {
		DeleteDialog(
			delete = {
				menuAction(
					MediaMenuActions.DeleteSongs(
						listOf(SongDetails(song.id, song.uri))
					)
				)
				openBottomSheet(false)
			},
			openDialog = { openDeleteDialog.value = it }
		)
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
					context.launchWriteSettingsUi(); openWriteSettingsUi.value = false
				} else { requestPermissionLauncher.launch(permission) }
			},
			openDialog = { openPermissionDialog.value = it; openWriteSettingsUi.value = it }
		)
	}
}

@Composable
fun SheetItem(
	icon: ImageVector,
	menuText: String,
	modifier: Modifier = Modifier,
	action: () -> Unit
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clickable { action() }
			.padding(horizontal = 20.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(modifier = Modifier.padding(start = 14.dp), imageVector = icon, contentDescription = null)
		Spacer(Modifier.width(20.dp))
		Text(text = menuText, fontSize = 20.sp)
	}
}

enum class MenuItems(
	val icon: ImageVector,
	val menuText: Int
) {
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.AddToQueue, R.string.add_to_queue),
	Rename(Icons.Rounded.Edit, R.string.rename),
	AddToFavorite(Icons.Rounded.Favorite, R.string.add_to_favorite),
	AddToPlaylist(Icons.Rounded.PlaylistAdd, R.string.add_to_playlist),
	SetAsRingtone(Icons.Rounded.AddAlert, R.string.set_as_ringtone),
	Share(Icons.Rounded.Share, R.string.share),
	GoToAlbum(Icons.Rounded.Album, R.string.go_to_album),
	GoToArtist(Icons.Rounded.Person, R.string.go_to_artist),
	Details(Icons.Rounded.Info, R.string.details),
	Delete(Icons.Rounded.Delete, R.string.delete)
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SheetPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState(true)
		val openBottomSheet = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.clickable { openBottomSheet.value = true }
				.fillMaxSize()
		) {
			if (openBottomSheet.value) {
				MediaItemSheet(
					song = previewData[6],
					scaffoldState = sheetState,
					goToCollection = { _, _ -> },
					openBottomSheet = { openBottomSheet.value = it },
					openAddToPlaylistDialog = {},
					menuAction = {}
				)
			}
		}
	}
}