package com.roland.android.odiyo.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.ui.components.CustomInputText
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.PlaylistMenuActions

@Composable
fun CreateOrRenamePlaylistDialog(
	playlist: Playlist?,
	listOfPlaylists: List<Playlist>,
	openPlaylist: (String, String) -> Unit,
	dialogAction: (PlaylistMenuActions, String) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	var playlistName by remember { mutableStateOf(playlist?.name ?: "") }

	AlertDialog(
		onDismissRequest = {},
		title = {
			Column {
				Text(
					text = stringResource(if (playlist == null) R.string.create_playlist else R.string.rename_playlist),
					modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
				OutlinedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 12.dp),
					value = playlistName,
					onValueChange = { playlistName = it },
					singleLine = true,
					shape = RoundedCornerShape(12.dp),
					textStyle = TextStyle(fontSize = 18.sp),
					label = { CustomInputText(stringResource(R.string.playlist)) }
				)
			}
		},
		confirmButton = {
			Button(
				enabled = playlistName.isNotEmpty() && playlistName != playlist?.name,
				onClick = {
					if (listOfPlaylists.any { it.name == playlistName }) return@Button
					if (playlist == null) {
						val createdPlaylist = Playlist(name = playlistName, songs = emptyList())
						dialogAction(PlaylistMenuActions.CreatePlaylist(createdPlaylist), createdPlaylist.name)
						openPlaylist(createdPlaylist.name, PLAYLISTS)
					} else {
						playlist.name = playlistName
						dialogAction(PlaylistMenuActions.RenamePlaylist(playlist), playlistName)
					}
					openDialog(false)
				}
			) {
				DialogButtonText(stringResource(if (playlist == null) R.string.create else R.string.rename))
			}
		},
		dismissButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.cancel))
			}
		}
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun CreateOrRenamePlaylistDialogPreview() {
	OdiyoTheme {
		val openDialog = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.fillMaxSize()
				.clickable { openDialog.value = true }
		) {
			if (openDialog.value) {
				CreateOrRenamePlaylistDialog(
					playlist = previewPlaylist[2],
					listOfPlaylists = previewPlaylist,
					openPlaylist = { _, _ -> },
					dialogAction = { _, _ -> }
				) { openDialog.value = it }
			}
		}
	}
}