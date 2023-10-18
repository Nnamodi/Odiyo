package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun PermissionDialog(
	icon: ImageVector = Icons.Rounded.MusicNote,
	iconColor: Color = Color.Blue,
	permissionMessage: String,
	requestPermission: () -> Unit,
	openDialog: (Boolean) -> Unit
) {
	AlertDialog(
		modifier = Modifier
			.fillMaxWidth(0.95f)
			.verticalScroll(rememberScrollState()),
		onDismissRequest = {},
		icon = {
			Icon(icon, null, Modifier.size(70.dp), iconColor)
		},
		title = {
			Text(
				text = permissionMessage,
				modifier = Modifier.padding(vertical = 20.dp),
				style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
			)
		},
		confirmButton = {
			TextButton(onClick = { requestPermission(); openDialog(false) }) {
				DialogButtonText(stringResource(R.string.continue_button))
			}
		},
		dismissButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.cancel))
			}
		}
	)
}

@Preview
@Composable
fun PermissionDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			PermissionDialog(
				permissionMessage = stringResource(R.string.read_storage_request),
				requestPermission = {},
				openDialog = {}
			)
		}
	}
}