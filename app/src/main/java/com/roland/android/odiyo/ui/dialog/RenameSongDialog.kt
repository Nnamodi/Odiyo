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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.domain.model.Music
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.CustomInputText
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun RenameSongDialog(
	song: Music,
	renameSong: (String, String) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	var songTitle by remember { mutableStateOf(song.title) }
	var songArtist by remember { mutableStateOf(song.artist) }

	AlertDialog(
		onDismissRequest = {},
		title = {
			Column {
				Text(stringResource(R.string.rename_song))
				OutlinedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 12.dp),
					value = songTitle,
					onValueChange = { songTitle = it },
					singleLine = true,
					shape = RoundedCornerShape(12.dp),
					textStyle = TextStyle(fontSize = 18.sp),
					label = { CustomInputText(stringResource(R.string.title)) }
				)
				OutlinedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 12.dp),
					value = songArtist,
					onValueChange = { songArtist = it },
					singleLine = true,
					shape = RoundedCornerShape(12.dp),
					textStyle = TextStyle(fontSize = 18.sp),
					label = { CustomInputText(stringResource(R.string.artist)) }
				)
			}
		},
		confirmButton = {
			Button(
				enabled = isRenamed(song.title, songTitle, song.artist, songArtist),
				onClick = {
					renameSong(songTitle, songArtist)
					openDialog(false)
				}
			) {
				DialogButtonText(stringResource(R.string.rename))
			}
		},
		dismissButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.cancel))
			}
		}
	)
}

fun isRenamed(
	initialTitle: String,
	title: String,
	initialArtist: String,
	artist: String,
): Boolean {
	return title.isNotEmpty() && artist.isNotBlank() &&
			initialTitle != title || initialArtist != artist
}

@Preview(showBackground = true)
@Composable
fun RenameSongDialogPreview() {
	OdiyoTheme {
		val openDialog = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.fillMaxSize()
				.clickable { openDialog.value = true }
		) {
			if (openDialog.value) {
				RenameSongDialog(
					song = previewData[6],
					renameSong = { _, _ -> },
					openDialog = { openDialog.value = it }
				)
			}
		}
	}
}