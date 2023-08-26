package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@OptIn(ExperimentalMaterial3Api::class)
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
			.clip(MaterialTheme.shapes.extraLarge)
			.background(AlertDialogDefaults.containerColor)
			.fillMaxWidth(0.95f),
		onDismissRequest = {}
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.padding(20.dp)
				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(icon, null, Modifier.size(70.dp), iconColor)
			Text(
				text = permissionMessage,
				modifier = Modifier.padding(vertical = 20.dp),
				style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
			)
			Row(Modifier.fillMaxWidth()) {
				Spacer(Modifier.weight(1f))
				TextButton(onClick = { openDialog(false) }) {
					DialogButtonText(stringResource(R.string.cancel))
				}
				TextButton(onClick = { requestPermission(); openDialog(false) }) {
					DialogButtonText(stringResource(R.string.continue_button))
				}
			}
		}
	}
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