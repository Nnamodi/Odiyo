package com.roland.android.odiyo.ui.dialog

import androidx.annotation.StringRes
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
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun ThemeDialog(
	@StringRes selectedTheme: Int,
	onThemeChanged: (Themes) -> Unit,
	closeDialog: () -> Unit
) {
	AlertDialog(
		onDismissRequest = closeDialog,
		title = {
			Text(text = stringResource(R.string.choose_theme))
		},
		text = {
			Column {
				Themes.values().forEach { theme ->
					Option(
						option = stringResource(theme.title),
						selected = selectedTheme == theme.title
					) {
						onThemeChanged(theme)
						closeDialog()
					}
				}
			}
		},
		confirmButton = {
			TextButton(onClick = closeDialog) {
				DialogButtonText(stringResource(R.string.close))
			}
		}
	)
}

enum class Themes(val title: Int) {
	System(R.string.system),
	Dark(R.string.dark_theme),
	Light(R.string.light_theme)
}

@Preview
@Composable
fun ThemeDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			ThemeDialog(Themes.System.title, {}) {}
		}
	}
}