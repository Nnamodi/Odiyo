package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.domain.model.Playlist
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewPlaylist
import com.roland.android.odiyo.ui.components.CustomInputText
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.screens.playlists.PlaylistMenuActions
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun CreateOrRenamePlaylistDialog(
	playlist: Playlist?,
	listOfPlaylists: List<Playlist>,
	openPlaylist: (String, String) -> Unit,
	dialogAction: (PlaylistMenuActions, String) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	var playlistName by remember { mutableStateOf(playlist?.name ?: "") }
	var playlistNameExists by remember { mutableStateOf(false) }

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
					onValueChange = { playlistName = it; playlistNameExists = false },
					singleLine = true,
					shape = RoundedCornerShape(12.dp),
					textStyle = TextStyle(fontSize = 18.sp),
					label = { CustomInputText(stringResource(R.string.playlist)) },
					supportingText = { if (playlistNameExists) Text(stringResource(R.string.name_already_exists, playlistName)) }
				)
			}
		},
		confirmButton = {
			Button(
				enabled = playlistName.isNotEmpty() && playlistName != playlist?.name,
				onClick = {
					if (listOfPlaylists.any { it.name == playlistName }) {
						playlistNameExists = true; return@Button
					}
					if (playlist == null) {
						val createdPlaylist = Playlist(name = playlistName, songs = emptyList())
						dialogAction(PlaylistMenuActions.CreatePlaylist(createdPlaylist), createdPlaylist.name)
						openPlaylist(createdPlaylist.name, PLAYLISTS)
					} else {
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