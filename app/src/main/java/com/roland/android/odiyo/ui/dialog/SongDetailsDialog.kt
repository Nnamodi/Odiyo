package com.roland.android.odiyo.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.MediaImage
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun SongDetailsDialog(song: Music, openDialog: (Boolean) -> Unit) {
	val context = LocalContext.current

	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(text = stringResource(R.string.details), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
		},
		text = {
			Column {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center
				) { MediaImage(modifier = Modifier.size(100.dp), artwork = song.getBitmap(context)) }
				Row { SongDetailText(song.name, Modifier.padding(vertical = 12.dp)) }
				Row { Text(stringResource(R.string.title_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.title) }
				Row { Text(stringResource(R.string.artist_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.artist) }
				Row { Text(stringResource(R.string.duration_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.duration) }
				Row { Text(stringResource(R.string.size_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.size) }
				Row { Text(stringResource(R.string.date_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.dateAdded) }
				Row { Text(stringResource(R.string.album_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.album) }
				Row { Text(stringResource(R.string.path_column)); Spacer(Modifier.width(16.dp)); SongDetailText(song.path) }
			}
		},
		confirmButton = {
			Button(
				modifier = Modifier.fillMaxWidth(),
				onClick = { openDialog(false) }
			) {
				DialogButtonText(stringResource(R.string.got_it))
			}
		}
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
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