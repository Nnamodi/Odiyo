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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.CustomInputText
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun CreatePlaylistDialog(
	createPlaylist: (String) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	var playlistName by remember { mutableStateOf("") }

	AlertDialog(
		onDismissRequest = {},
		title = {
			Column {
				Text(stringResource(R.string.create_playlist))
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
				enabled = playlistName.isNotEmpty(),
				onClick = {
					createPlaylist(playlistName)
					openDialog(false)
				}
			) {
				DialogButtonText(stringResource(R.string.create))
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
fun CreatePlaylistDialogPreview() {
	OdiyoTheme {
		val openDialog = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.fillMaxSize()
				.clickable { openDialog.value = true }
		) {
			if (openDialog.value) {
				CreatePlaylistDialog(
					createPlaylist = {},
					openDialog = { openDialog.value = it }
				)
			}
		}
	}
}