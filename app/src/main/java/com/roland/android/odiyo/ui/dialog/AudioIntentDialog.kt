package com.roland.android.odiyo.ui.dialog

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddToQueue
import androidx.compose.material.icons.rounded.HelpOutline
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
import com.roland.android.odiyo.util.SettingsActions

@Composable
fun AudioIntentDialog(
	uri: Uri,
	parentIsSettings: Boolean = false,
	@StringRes selectedOption: Int? = null,
	intentAction: (AudioIntentActions) -> Unit = {},
	onSelected: (SettingsActions) -> Unit = {},
	openDialog: () -> Unit
) {
	val optionsMenu = IntentOptions.values().toMutableList()
	if (!parentIsSettings) optionsMenu.remove(IntentOptions.AlwaysAsk)

	AlertDialog(
		onDismissRequest = { if (parentIsSettings) openDialog() },
		title = {
			Text(stringResource(
				if (parentIsSettings) R.string.how_will_you_handle_it else R.string.audio_intent_message
			))
		},
		text = {
			Column(Modifier.verticalScroll(rememberScrollState())) {
				optionsMenu.forEach { option ->
					val action = when (option) {
						IntentOptions.Play -> AudioIntentActions.Play(uri)
						IntentOptions.PlayNext -> AudioIntentActions.PlayNext(uri)
						IntentOptions.AddToQueue -> AudioIntentActions.AddToQueue(uri)
						else -> null
					}

					SheetItem(
						icon = option.icon,
						menuText = stringResource(option.menuText),
						modifier = Modifier.clip(MaterialTheme.shapes.medium),
						selected = selectedOption == option.menuText
					) {
						if (parentIsSettings) {
							onSelected(SettingsActions.SetIntentOption(option))
							openDialog()
						} else action?.let(intentAction)
					}
				}
			}
		},
		confirmButton = {
			TextButton(onClick = openDialog) {
				DialogButtonText(stringResource(
					id = if (parentIsSettings) R.string.close else R.string.drop_it
				))
			}
		}
	)
}

enum class IntentOptions(val icon: ImageVector, val menuText: Int) {
	Play(Icons.Rounded.PlayArrow, R.string.play),
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.AddToQueue, R.string.add_to_queue),
	AlwaysAsk(Icons.Rounded.HelpOutline, R.string.always_ask)
}

@Preview
@Composable
fun AudioIntentDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			AudioIntentDialog("null".toUri(), intentAction = {}) {}
		}
	}
}