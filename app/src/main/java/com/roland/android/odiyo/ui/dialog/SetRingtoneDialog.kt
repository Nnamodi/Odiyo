package com.roland.android.odiyo.ui.dialog

import android.media.RingtoneManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@Composable
fun SetRingtoneDialog(
	song: Music,
	onRingtoneSet: (MediaMenuActions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(text = stringResource(R.string.set_as))
		},
		text = {
			Column {
				RingtoneOptions.values().forEach { option ->
					SortOption(
						option = stringResource(option.title),
						selected = false
					) {
						onRingtoneSet(MediaMenuActions.SetAsRingtone(song, option.ringType))
						openDialog(false)
					}
				}
			}
		},
		confirmButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.close))
			}
		}
	)
}

enum class RingtoneOptions(val title: Int, val ringType: Int) {
	Ringtone(R.string.ringtone, RingtoneManager.TYPE_RINGTONE),
	Alarm(R.string.alarm_sound, RingtoneManager.TYPE_ALARM),
	Notification(R.string.notification_tone, RingtoneManager.TYPE_NOTIFICATION),
}

@Preview
@Composable
fun SetRingtoneDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SetRingtoneDialog(song = previewData[3], onRingtoneSet = {}, openDialog = {})
		}
	}
}