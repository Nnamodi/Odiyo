package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun DeleteDialog(
	delete: () -> Unit,
	openDialog: (Boolean) -> Unit,
	itemIsPlaylist: Boolean = false
) {
	val itemToDelete = stringResource(if (itemIsPlaylist) R.string.playlist else R.string.song)

	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = { Text(stringResource(R.string.delete)) },
		text = { Text(stringResource(R.string.delete_dialog_text, itemToDelete)) },
		confirmButton = {
			TextButton(
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
				onClick = { delete(); openDialog(false) }
			) {
				DialogButtonText(stringResource(R.string.delete))
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