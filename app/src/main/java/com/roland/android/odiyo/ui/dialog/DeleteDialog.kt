package com.roland.android.odiyo.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun DeleteDialog(
	@StringRes title: Int = R.string.delete,
	@StringRes text: Int = R.string.delete_dialog_text,
	@StringRes deleteText: Int = R.string.delete,
	delete: () -> Unit,
	openDialog: (Boolean) -> Unit,
	itemIsPlaylist: Boolean = false,
	multipleSongs: Boolean = false
) {
	val itemToDelete = stringResource(
		when {
			itemIsPlaylist -> R.string.this_playlist
			multipleSongs -> R.string.these_songs
			else -> R.string.this_song
		}
	)

	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = { Text(stringResource(title)) },
		text = { Text(stringResource(text, itemToDelete)) },
		confirmButton = {
			TextButton(
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
				onClick = { delete(); openDialog(false) }
			) {
				DialogButtonText(stringResource(deleteText))
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
fun DeleteDialogPreview() {
	OdiyoTheme {
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			DeleteDialog(
				delete = {},
				openDialog = {}
			)
		}
	}
}