package com.roland.android.odiyo.ui.sheets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.dialog.DeleteSongDialog
import com.roland.android.odiyo.ui.dialog.RenameSongDialog
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.sheets.MenuItems.*
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
	goToCollection: (String, String) -> Unit,
	openBottomSheet: (Boolean) -> Unit,
	menuAction: (MediaMenuActions) -> Unit
) {
	val screenHeight = LocalConfiguration.current.screenHeightDp / 2
	val openRenameDialog = remember { mutableStateOf(false) }
	val openDetailsDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }

	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
	) {
		val menuItems = MenuItems.values()

		Column(
			modifier = Modifier
				.height(screenHeight.dp)
				.verticalScroll(rememberScrollState())
		) {
			menuItems.forEach { menu ->
				val action = { when (menu) {
					PlayNext -> { menuAction(MediaMenuActions.PlayNext(listOf(song))); openBottomSheet(false) }
					AddToQueue -> { menuAction(MediaMenuActions.AddToQueue(listOf(song))); openBottomSheet(false) }
					Rename -> openRenameDialog.value = true
					Share -> menuAction(MediaMenuActions.ShareSong(song))
					GoToAlbum -> { goToCollection(song.album, ALBUMS); openBottomSheet(false) }
					GoToArtist -> { goToCollection(song.artist, ARTISTS); openBottomSheet(false) }
					Details -> openDetailsDialog.value = true
					Delete -> openDeleteDialog.value = true
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
	AddToQueue(Icons.Rounded.PlaylistAdd, R.string.add_to_queue),
	Rename(Icons.Rounded.Edit, R.string.rename),
	Share(Icons.Rounded.Share, R.string.share),
	GoToAlbum(Icons.Rounded.Album, R.string.go_to_album),
	GoToArtist(Icons.Rounded.Person, R.string.go_to_artist),
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
					menuAction = {}
				)
			}
		}
	}
}