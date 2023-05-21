package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun DeleteSongDialog(deleteSong: () -> Unit, openDialog: (Boolean) -> Unit) {
	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = { Text("Delete") },
		text = { Text("You will not be able to recover this song again") },
		confirmButton = {
			TextButton(
				colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
				onClick = { deleteSong(); openDialog(false) }
			) {
				DialogButtonText("Delete")
			}
		},
		dismissButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText("Cancel")
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun DeleteSongDialogPreview() {
	OdiyoTheme {
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			DeleteSongDialog(
				deleteSong = {},
				openDialog = {}
			)
		}
	}
}