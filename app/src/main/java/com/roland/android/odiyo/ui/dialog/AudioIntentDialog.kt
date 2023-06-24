package com.roland.android.odiyo.ui.dialog

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddToQueue
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.sheets.SheetItem
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.AudioIntentActions

@Composable
fun AudioIntentDialog(
	uri: Uri,
	intentAction: (AudioIntentActions) -> Unit,
	openDialog: () -> Unit
) {
	AlertDialog(
		onDismissRequest = {},
		title = { Text(stringResource(R.string.audio_intent_message)) },
		text = {
			Column(Modifier.verticalScroll(rememberScrollState())) {
				IntentOptionItems.values().forEach { option ->
					val action = when (option) {
						IntentOptionItems.Play -> AudioIntentActions.Play(uri)
						IntentOptionItems.PlayNext -> AudioIntentActions.PlayNext(uri)
						IntentOptionItems.AddToQueue -> AudioIntentActions.AddToQueue(uri)
					}

					IntentOptions(option.icon, option.menuText) { intentAction(action) }
				}
			}
		},
		confirmButton = {
			TextButton(onClick = openDialog) {
				DialogButtonText(stringResource(R.string.drop_it))
			}
		}
	)
}

@Composable
fun IntentOptions(icon: ImageVector, menuText: Int, action: () -> Unit) =
	SheetItem(
		icon = icon,
		menuText = stringResource(menuText),
		modifier = Modifier.clip(MaterialTheme.shapes.medium),
		action = action
	)

enum class IntentOptionItems(val icon: ImageVector, val menuText: Int) {
	Play(Icons.Rounded.PlayArrow, R.string.play),
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.AddToQueue, R.string.add_to_queue)
}

@Preview
@Composable
fun AudioIntentDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			AudioIntentDialog("null".toUri(), {}) {}
		}
	}
}