package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.MenuItems.*
import com.roland.android.odiyo.ui.dialog.DeleteSongDialog
import com.roland.android.odiyo.ui.dialog.RenameSongDialog
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SongDetails

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun MediaItemSheet(
	song: Music,
	scaffoldState: SheetState,
	openBottomSheet: (Boolean) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
) {
	val openRenameDialog = remember { mutableStateOf(false) }
	val openDetailsDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }

	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
	) {
		val menuItems = listOf(PlayNext, Rename, Share, Details, Delete)

		Column(Modifier.padding(bottom = 20.dp)) {
			menuItems.forEachIndexed { index, menu ->
				val action = { when (index) {
					0 -> { menuAction(MediaMenuActions.PlayNext(song)); openBottomSheet(false) }
					1 -> openRenameDialog.value = true
					2 -> menuAction(MediaMenuActions.ShareSong(song))
					3 -> openDetailsDialog.value = true
					4 -> openDeleteDialog.value = true
					else -> {}
				} }
				SheetItem(menu.icon, stringResource(menu.menuText)) { action() }
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
		DeleteSongDialog(
			deleteSong = {
				menuAction(
					MediaMenuActions.DeleteSong(
						SongDetails(song.id, song.uri)
					)
				)
				openBottomSheet(false)
			},
			openDialog = { openDeleteDialog.value = it }
		)
	}
}

@Composable
fun SheetItem(icon: ImageVector, menuText: String, action: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { action() }
			.padding(20.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(modifier = Modifier.padding(start = 14.dp), imageVector = icon, contentDescription = null)
		Spacer(Modifier.width(20.dp))
		Text(
			text = menuText,
			fontSize = TextUnit(20f, Sp)
		)
	}
}

enum class MenuItems(
	val icon: ImageVector,
	val menuText: Int
) {
	PlayNext(Icons.Rounded.PlaylistAdd, R.string.play_next),
	Rename(Icons.Rounded.Edit, R.string.rename),
	Share(Icons.Rounded.Share, R.string.share),
	Details(Icons.Rounded.Info, R.string.details),
	Delete(Icons.Rounded.Delete, R.string.delete)
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Preview(showBackground = true)
@Composable
fun SheetPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState()
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
					openBottomSheet = { openBottomSheet.value = it },
					menuAction = {}
				)
			}
		}
	}
}