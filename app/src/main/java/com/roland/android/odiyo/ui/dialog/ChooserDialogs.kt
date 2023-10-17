package com.roland.android.odiyo.ui.dialog

import android.media.RingtoneManager
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@Composable
fun LanguageChooserDialog(
	selectedOption: String,
	onLanguagePicked: (String?) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	ChooserDialog(
		title = R.string.choose_language,
		selectedOption = selectedOption,
		languageOptions = LanguageOptions.values(),
		onLanguagePicked = onLanguagePicked,
		openDialog = openDialog
	)
}

@Composable
fun SetRingtoneDialog(
	song: Music,
	onRingtoneSet: (MediaMenuActions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	ChooserDialog(
		title = R.string.set_as,
		selectedOption = null,
		song = song,
		ringtoneOptions = RingtoneOptions.values(),
		onRingPicked = onRingtoneSet,
		openDialog = openDialog
	)
}

@Composable
fun SortDialog(
	selectedOption: SortOptions,
	onSortPicked: (SortOptions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	ChooserDialog(
		title = R.string.sort_by,
		selectedOption = selectedOption,
		sortOptions = SortOptions.values(),
		onSortPicked = onSortPicked,
		openDialog = openDialog
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
		themeOptions = Themes.values(),
		onThemePicked = onThemeChanged
	) { closeDialog() }
}

@Composable
private fun <T>ChooserDialog(
	@StringRes title: Int,
	selectedOption: T?,
	song: Music? = null,
	languageOptions: Array<LanguageOptions>? = null,
	ringtoneOptions: Array<RingtoneOptions>? = null,
	sortOptions: Array<SortOptions>? = null,
	themeOptions: Array<Themes>? = null,
	onLanguagePicked: (String?) -> Unit = {},
	onRingPicked: (MediaMenuActions) -> Unit = {},
	onSortPicked: (SortOptions) -> Unit = {},
	onThemePicked: (Themes) -> Unit = {},
	openDialog: (Boolean) -> Unit
) {
	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(text = stringResource(title))
		},
		text = {
			Column {
				languageOptions?.let {
					it.forEach { option ->
						Option(
							option = stringResource(option.title),
							selected = selectedOption == option.local
						) {
							onLanguagePicked(option.local)
							openDialog(false)
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
							openDialog(false)
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
							openDialog(false)
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
							openDialog(false)
						}
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

enum class LanguageOptions(val title: Int, val local: String) {
	FollowSystem(R.string.follow_system, ""),
	English(R.string.english, "en"),
	French(R.string.french, "fr")
}

private enum class RingtoneOptions(val title: Int, val ringType: Int) {
	Ringtone(R.string.ringtone, RingtoneManager.TYPE_RINGTONE),
	Alarm(R.string.alarm_sound, RingtoneManager.TYPE_ALARM),
	Notification(R.string.notification_tone, RingtoneManager.TYPE_NOTIFICATION),
}

enum class SortOptions(val title: Int) {
	NameAZ(R.string.name_a_z),
	NameZA(R.string.name_z_a),
	NewestFirst(R.string.newest_first),
	OldestFirst(R.string.oldest_first)
}

enum class Themes(val title: Int) {
	System(R.string.follow_system),
	Dark(R.string.dark_theme),
	Light(R.string.light_theme)
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