package com.roland.android.odiyo.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SongDetailsDialog(song: Music, openDialog: (Boolean) -> Unit) {
	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(text = "Details", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
		},
		text = {
			Column {
				Row { SongDetailText(song.name, Modifier.padding(bottom = 12.dp)) }
				Row { Text("Title:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.title) }
				Row { Text("Artist:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.artist) }
				Row { Text("Duration:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.duration) }
				Row { Text("Size:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.size) }
				Row { Text("Added:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.dateAdded) }
				Row { Text("Album:"); Spacer(Modifier.width(16.dp)); SongDetailText(song.album) }
			}
		},
		confirmButton = {
			Button(
				modifier = Modifier.fillMaxWidth(),
				onClick = { openDialog(false) }
			) {
				DialogButtonText("Got it")
			}
		}
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun SongDetailsDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SongDetailsDialog(
				openDialog = {},
				song = previewData[4]
			)
		}
	}
}