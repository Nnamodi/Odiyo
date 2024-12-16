package com.roland.android.odiyo.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.domain.model.Music
import com.roland.android.domain.util.LanguageOptions
import com.roland.android.domain.util.RingtoneOptions
import com.roland.android.domain.util.SortOptions
import com.roland.android.domain.util.Themes
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.actions.MediaMenuActions
import kotlin.enums.EnumEntries

@Composable
fun LanguageChooserDialog(
	selectedOption: String,
	onLanguagePicked: (String?) -> Unit,
	closeDialog: () -> Unit
) {
	ChooserDialog(
		title = R.string.choose_language,
		selectedOption = selectedOption,
		languageOptions = LanguageOptions.entries,
		onLanguagePicked = onLanguagePicked,
		closeDialog = closeDialog
	)
}

@Composable
fun SetRingtoneDialog(
	song: Music,
	onRingtoneSet: (MediaMenuActions) -> Unit,
	closeDialog: () -> Unit
) {
	ChooserDialog(
		title = R.string.set_as,
		selectedOption = null,
		song = song,
		ringtoneOptions = RingtoneOptions.entries,
		onRingPicked = onRingtoneSet,
		closeDialog = closeDialog
	)
}

@Composable
fun SortDialog(
	selectedOption: SortOptions,
	onSortPicked: (SortOptions) -> Unit,
	closeDialog: () -> Unit
) {
	ChooserDialog(
		title = R.string.sort_by,
		selectedOption = selectedOption,
		sortOptions = SortOptions.entries,
		onSortPicked = onSortPicked,
		closeDialog = closeDialog
	)
}

@Composable
fun ThemeDialog(
	@StringRes selectedTheme: Int,
	onThemeChanged: (Themes) -> Unit,
	closeDialog: () -> Unit
) {
	ChooserDialog(
		title = R.string.choose_theme,
		selectedOption = selectedTheme,
		themeOptions = Themes.entries,
		onThemePicked = onThemeChanged
	) { closeDialog() }
}

@Composable
private fun <T>ChooserDialog(
	@StringRes title: Int,
	selectedOption: T?,
	song: Music? = null,
	languageOptions: EnumEntries<LanguageOptions>? = null,
	ringtoneOptions: EnumEntries<RingtoneOptions>? = null,
	sortOptions: EnumEntries<SortOptions>? = null,
	themeOptions: EnumEntries<Themes>? = null,
	onLanguagePicked: (String) -> Unit = {},
	onRingPicked: (MediaMenuActions) -> Unit = {},
	onSortPicked: (SortOptions) -> Unit = {},
	onThemePicked: (Themes) -> Unit = {},
	closeDialog: () -> Unit
) {
	AlertDialog(
		onDismissRequest = { closeDialog() },
		title = {
			Text(text = stringResource(title))
		},
		text = {
			Column(Modifier.verticalScroll(rememberScrollState())) {
				languageOptions?.let {
					it.forEach { option ->
						Option(
							option = stringResource(option.title),
							selected = selectedOption == option.local
						) {
							onLanguagePicked(option.local)
							closeDialog()
						}
					}
				}
				ringtoneOptions?.let {
					it.forEach { option ->
						Option(
							option = stringResource(option.title),
							selected = false
						) {
							song?.let { song ->
								onRingPicked(MediaMenuActions.SetAsRingtone(song, option.ringType))
							}
							closeDialog()
						}
					}
				}
				sortOptions?.let {
					it.forEach { option ->
						Option(
							option = stringResource(option.title),
							selected = selectedOption == option
						) {
							onSortPicked(option)
							closeDialog()
						}
					}
				}
				themeOptions?.let {
					it.forEach { option ->
						Option(
							option = stringResource(option.title),
							selected = (selectedOption as Int) == option.title
						) {
							onThemePicked(option)
							closeDialog()
						}
					}
				}
			}
		},
		confirmButton = {
			TextButton(onClick = { closeDialog() }) {
				DialogButtonText(stringResource(R.string.close))
			}
		}
	)
}

@Composable
private fun Option(
	option: String,
	selected: Boolean,
	action: () -> Unit
) {
	val color = if (selected) MaterialTheme.colorScheme.primary else AlertDialogDefaults.textContentColor

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(MaterialTheme.shapes.medium)
			.clickable { action() }
			.padding(horizontal = 8.dp, vertical = 10.dp),
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = option,
			modifier = Modifier
				.weight(1f)
				.padding(vertical = 2.dp),
			color = color,
			style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Normal)
		)
		if (selected) {
			Icon(
				imageVector = Icons.Rounded.Done,
				contentDescription = stringResource(R.string.sort_option_selected, option),
				tint = color
			)
		}
	}
}

@Preview
@Composable
private fun LanguageChooserDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			LanguageChooserDialog(selectedOption = "", {}) {}
		}
	}
}

@Preview
@Composable
private fun SetRingtoneDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SetRingtoneDialog(previewData[3], {}) {}
		}
	}
}

@Preview
@Composable
private fun SortDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SortDialog(SortOptions.NameAZ, {}) {}
		}
	}
}

@Preview
@Composable
private fun ThemeDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			ThemeDialog(Themes.System.title, {}) {}
		}
	}
}