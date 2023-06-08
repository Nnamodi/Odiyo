package com.roland.android.odiyo.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.screens.CreatePlaylistButton
import com.roland.android.odiyo.ui.screens.PlaylistItem
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AddToPlaylistDialog(
	song: Music,
	playlists: List<Playlist>,
	addSongToPlaylist: (MediaMenuActions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	val openPlaylistDialog = remember { mutableStateOf(false) }
	val dialogMaxHeight = LocalConfiguration.current.screenHeightDp * 0.65

	if (!openPlaylistDialog.value) {
		AlertDialog(
			modifier = Modifier
				.clip(MaterialTheme.shapes.extraLarge)
				.background(AlertDialogDefaults.containerColor),
			onDismissRequest = { openDialog(false) }
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 24.dp)
			) {
				Text(
					text = stringResource(R.string.choose_playlist),
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 10.dp),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.headlineSmall
				)
				LazyColumn(Modifier.heightIn(min = 10.dp, max = dialogMaxHeight.dp)) {
					item { CreatePlaylistButton { openPlaylistDialog.value = true } }
					itemsIndexed(
						items = playlists,
						key = { _, playlist -> playlist.id }
					) { _, playlist ->
						PlaylistItem(
							playlist = playlist,
							onItemClick = { _, _ ->
								addSongToPlaylist(MediaMenuActions.AddToPlaylist(song, playlist))
								openDialog(false)
							},
							parentContentIsDialog = true,
							openMenuSheet = {}
						)
					}
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(12.dp)
				) {
					Spacer(Modifier.weight(1f))
					TextButton(onClick = { openDialog(false) }) {
						DialogButtonText(stringResource(R.string.cancel))
					}
				}
			}
		}
	} else {
		CreateOrRenamePlaylistDialog(
			playlist = null,
			openPlaylist = { playlistName, _ ->
				val playlist = Playlist(name = playlistName, songs = listOf(song.uri), numSongs = 1)
				addSongToPlaylist(MediaMenuActions.AddToPlaylist(null, playlist))
				openDialog(false)
			},
			dialogAction = {  },
			openDialog = { openDialog(it) }
		)
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun AddToPlaylistDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			AddToPlaylistDialog(
				song = previewData[0],
				playlists = previewPlaylist.take(2),
				addSongToPlaylist = {},
				openDialog = {}
			)
		}
	}
}