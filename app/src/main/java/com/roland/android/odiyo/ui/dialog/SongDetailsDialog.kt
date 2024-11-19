package com.roland.android.odiyo.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.roland.android.domain.model.Music
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.SongDetailText
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.time
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun SongDetailsDialog(
	song: Music,
	openDialog: (Boolean) -> Unit
) {
	val size = if (song.uri.toString().isEmpty()) "--" else song.size
	val dateAdded = if (song.uri.toString().isEmpty()) "--" else song.addedOn

	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(
				text = stringResource(R.string.details),
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center
			)
		},
		text = {
			Column(Modifier.verticalScroll(rememberScrollState())) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center
				) {
					MediaImage(
						modifier = Modifier.size(100.dp),
						artwork = song.artwork
					)
				}
				RowInfo(detailText = song.name, detailTextPadding = 12.dp)
				RowInfo(R.string.title_column, song.title)
				RowInfo(R.string.artist_column, song.artist)
				RowInfo(R.string.duration_column, song.duration.time)
				RowInfo(R.string.size_column, size)
				RowInfo(R.string.date_column, dateAdded)
				RowInfo(R.string.album_column, song.album)
				RowInfo(R.string.path_column, song.path)
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

@Composable
private fun RowInfo(
	@StringRes title: Int? = null,
	detailText: String,
	detailTextPadding: Dp = 0.dp
) {
	Row {
		title?.let { Text(stringResource(it)) }
		Spacer(Modifier.width(16.dp))
		SongDetailText(
			text = detailText,
			modifier = Modifier.padding(vertical = detailTextPadding)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun SongDetailsDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SongDetailsDialog(previewData[4]) {}
		}
	}
}