package com.roland.android.odiyo.ui.sheets

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.ui.dialog.CreateOrRenamePlaylistDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.screens.PlaylistItem
import com.roland.android.odiyo.ui.sheets.PlaylistMenuItems.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.PlaylistMenuActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistItemSheet(
	playlist: Playlist,
	listOfPlaylists: List<Playlist>,
	scaffoldState: SheetState,
	openBottomSheet: (Boolean) -> Unit,
	openPlaylist: (String, String) -> Unit,
	playlistMenuAction: (PlaylistMenuActions) -> Unit
) {
	val openPlaylistDialog = remember { mutableStateOf(false) }
	val openDeleteDialog = remember { mutableStateOf(false) }

	ModalBottomSheet(
		modifier = Modifier.absoluteOffset(y = 16.dp),
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
		dragHandle = {
			PlaylistItem(
				modifier = Modifier
					.padding(10.dp)
					.clip(BottomSheetDefaults.ExpandedShape),
				playlist = playlist,
				onItemClick = { _, _ -> },
				parentContentIsDialog = true
			) {}
		}
	) {
		val menuItems = PlaylistMenuItems.values().toMutableList()
		if (playlist.numOfSongs() == 0) menuItems.removeAll(setOf(PlayNext, AddToQueue))

		Column(
			modifier = Modifier
				.wrapContentHeight()
				.verticalScroll(rememberScrollState())
		) {
			menuItems.forEach { menu ->
				val action = { when (menu) {
					PlayNext -> { playlistMenuAction(PlaylistMenuActions.PlayNext(playlist)); openBottomSheet(false) }
					AddToQueue -> { playlistMenuAction(PlaylistMenuActions.AddToQueue(playlist)); openBottomSheet(false) }
					Rename -> openPlaylistDialog.value = true
					Delete -> openDeleteDialog.value = true
				} }
				SheetItem(menu.icon, stringResource(menu.menuText)) { action() }
			}
		}
	}

	if (openPlaylistDialog.value) {
		CreateOrRenamePlaylistDialog(
			playlist = playlist,
			listOfPlaylists = listOfPlaylists,
			openPlaylist = openPlaylist,
			dialogAction = { action, _ ->
				playlistMenuAction(action)
				openBottomSheet(false)
			}
		) { openPlaylistDialog.value = it }
	}

	if (openDeleteDialog.value) {
		DeleteDialog(
			delete = {
				playlistMenuAction(PlaylistMenuActions.DeletePlaylist(playlist))
				openBottomSheet(false)
			},
			openDialog = { openDeleteDialog.value = it },
			itemIsPlaylist = true
		)
	}
}

enum class PlaylistMenuItems(val icon: ImageVector, val menuText: Int) {
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.AddToQueue, R.string.add_to_queue),
	Rename(Icons.Rounded.Edit, R.string.rename),
	Delete(Icons.Rounded.Delete, R.string.delete)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PlaylistItemSheetPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState(true)
		val openBottomSheet = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.clickable { openBottomSheet.value = true }
				.fillMaxSize()
		) {
			if (openBottomSheet.value) {
				PlaylistItemSheet(
					playlist = previewPlaylist[3],
					listOfPlaylists = previewPlaylist,
					scaffoldState = sheetState,
					openBottomSheet = { openBottomSheet.value = it },
					openPlaylist = { _, _ -> }
				) {}
			}
		}
	}
}