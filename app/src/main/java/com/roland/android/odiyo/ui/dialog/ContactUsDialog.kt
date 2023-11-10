package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.SettingsActions

@Composable
fun ContactUsDialog(
	launchEmailApp: (SettingsActions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(stringResource(R.string.contact_us))
		},
		text = {
			val context = LocalContext.current
			val contactAddress = stringResource(R.string.contact_address_info)

			Column {
				Text(stringResource(R.string.contact_message))
				Text(
					text = stringResource(R.string.contact_address),
					fontWeight = FontWeight.SemiBold,
					style = MaterialTheme.typography.bodyLarge
				)
				Text(
					text = contactAddress,
					modifier = Modifier.clickable {
						launchEmailApp(SettingsActions.ContactUs(context, contactAddress))
					},
					color = MaterialTheme.colorScheme.primary,
					textDecoration = TextDecoration.Underline
				)
			}
		},
		confirmButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.close))
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun ContactUsDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			ContactUsDialog({}, {})
		}
	}
}