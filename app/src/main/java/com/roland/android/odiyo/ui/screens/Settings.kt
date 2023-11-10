package com.roland.android.odiyo.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.ContactMail
import androidx.compose.material.icons.rounded.ContactSupport
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.ManageHistory
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.WbIncandescent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.roland.android.odiyo.R
import com.roland.android.odiyo.states.SettingsUiState
import com.roland.android.odiyo.ui.components.AppBar
import com.roland.android.odiyo.ui.dialog.AudioIntentDialog
import com.roland.android.odiyo.ui.dialog.ContactUsDialog
import com.roland.android.odiyo.ui.dialog.DeleteDialog
import com.roland.android.odiyo.ui.dialog.LanguageChooserDialog
import com.roland.android.odiyo.ui.dialog.LanguageOptions
import com.roland.android.odiyo.ui.dialog.ThemeDialog
import com.roland.android.odiyo.ui.navigation.ABOUT_US
import com.roland.android.odiyo.ui.navigation.SUPPORT
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.SettingsActions
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
	uiState: SettingsUiState,
	settingsAction: (SettingsActions) -> Unit,
	navigateToAboutUsScreen: (String) -> Unit,
	navigateUp: () -> Unit
) {
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val openThemeDialog = remember { mutableStateOf(false) }
	val openClearHistoryDialog = remember { mutableStateOf(false) }
	val openMusicIntentDialog = remember { mutableStateOf(false) }
	val openLanguageChooserDialog = remember { mutableStateOf(false) }
	val openContactUsDialog = remember { mutableStateOf(false) }
	val appLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()

	Scaffold(
		topBar = {
			AppBar(navigateUp = navigateUp, title = stringResource(R.string.settings))
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.verticalScroll(rememberScrollState())
		) {
			val displayCategory by remember { mutableStateOf(optionsCategory(R.string.display_category)) }
			val searchHistoryCategory by remember { mutableStateOf(optionsCategory(R.string.search_history_category)) }
			val preferencesCategory by remember { mutableStateOf(optionsCategory(R.string.preferences_category)) }
			val aboutUsCategory by remember { mutableStateOf(optionsCategory(R.string.about_category)) }

			Container(displayCategory[0].category) {
				displayCategory.forEach {
					val optionIsTheme = it.name == OptionsMenu.Theme.name
					val subTitle = when {
						optionIsTheme -> uiState.theme
						else -> null
					}
					val action = { openThemeDialog.value = true }
					SettingsOption(
						leadingIcon = it.icon,
						option = it.option,
						subTitle = subTitle,
						invertIcon = optionIsTheme
					) { action() }
				}
			}
			Container(searchHistoryCategory[0].category) {
				searchHistoryCategory.forEach { menu ->
					val trailingIcon = if (menu == OptionsMenu.SaveSearchHistory) {
						if (uiState.shouldSaveSearchHistory) Icons.Rounded.Done else Icons.Rounded.Clear
					} else null
					val noHistoryToast = Toast.makeText(context, R.string.no_history, Toast.LENGTH_SHORT)
					val action = {
						when (menu) {
							OptionsMenu.SaveSearchHistory -> settingsAction(SettingsActions.SaveSearchHistory)
							OptionsMenu.ClearSearchHistory -> if (uiState.searchHistoryEmpty) noHistoryToast.show() else openClearHistoryDialog.value = true
							else -> null
						}
					}
					SettingsOption(
						leadingIcon = menu.icon,
						trailingIcon = trailingIcon,
						option = menu.option
					) { action() }
				}
			}
			Container(preferencesCategory[0].category) {
				preferencesCategory.forEach { menu ->
					val subTitle = when (menu) {
						OptionsMenu.HowToHandleMusicIntent -> uiState.musicIntentOption
						OptionsMenu.Language -> appLocale.getLanguage()
						else -> null
					}
					val action = { when (menu) {
						OptionsMenu.HowToHandleMusicIntent -> openMusicIntentDialog.value = true
						OptionsMenu.Language -> openLanguageChooserDialog.value = true
						else -> null
					} }
					SettingsOption(
						leadingIcon = menu.icon,
						option = menu.option,
						subTitle = subTitle
					) { action() }
				}
			}
			Container(aboutUsCategory[0].category) {
				aboutUsCategory.forEach { menu ->
					val action = { when (menu) {
						OptionsMenu.AboutUs -> navigateToAboutUsScreen(ABOUT_US)
						OptionsMenu.Support -> navigateToAboutUsScreen(SUPPORT)
						OptionsMenu.ContactUs -> openContactUsDialog.value = true
						else -> {}
					} }
					SettingsOption(leadingIcon = menu.icon, option = menu.option) {
						action()
					}
				}
			}
			Spacer(Modifier.height(100.dp))
		}
	}

	if (openThemeDialog.value) {
		ThemeDialog(
			selectedTheme = uiState.theme,
			onThemeChanged = { settingsAction(SettingsActions.SetTheme(it)) },
			closeDialog = { openThemeDialog.value = false }
		)
	}

	if (openClearHistoryDialog.value) {
		DeleteDialog(
			parentIsSettings = true,
			delete = {
				settingsAction(SettingsActions.ClearSearchHistory)
				scope.launch {
					snackbarHostState.showSnackbar(context.getString(R.string.history_cleared))
				}
			},
			openDialog = { openClearHistoryDialog.value = it }
		)
	}

	if (openMusicIntentDialog.value) {
		AudioIntentDialog(
			uri = "null".toUri(),
			parentIsSettings = true,
			selectedOption = uiState.musicIntentOption,
			onSelected = settingsAction,
			openDialog = { openMusicIntentDialog.value = false }
		)
	}

	if (openLanguageChooserDialog.value) {
		LanguageChooserDialog(
			selectedOption = appLocale,
			onLanguagePicked = {
				val locale = LocaleListCompat.forLanguageTags(it)
				AppCompatDelegate.setApplicationLocales(locale)
			},
			openDialog = { openLanguageChooserDialog.value = false }
		)
	}

	if (openContactUsDialog.value) {
		ContactUsDialog(settingsAction) { openContactUsDialog.value = it }
	}
}

@Composable
private fun SettingsOption(
	modifier: Modifier = Modifier,
	leadingIcon: ImageVector,
	trailingIcon: ImageVector? = null,
	@StringRes option: Int,
	@StringRes subTitle: Int? = null,
	invertIcon: Boolean = false,
	action: () -> Unit
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp)
			.clip(MaterialTheme.shapes.large)
			.clickable { action() }
			.padding(horizontal = 20.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			modifier = Modifier
				.padding(start = 14.dp, end = 20.dp)
				.then(if (invertIcon) Modifier.rotate(180f) else Modifier),
			imageVector = leadingIcon, contentDescription = null
		)
		Column {
			Text(text = stringResource(option), fontSize = 20.sp)
			subTitle?.let {
				Text(
					text = stringResource(it),
					modifier = Modifier.alpha(0.5f),
					style = MaterialTheme.typography.titleMedium
				)
			}
		}
		Spacer(Modifier.weight(1f))
		trailingIcon?.let { icon ->
			val description = if (icon == Icons.Rounded.Done) R.string.history_is_being_saved else R.string.history_is_not_saved
			Icon(
				modifier = Modifier.padding(horizontal = 14.dp),
				imageVector = icon, contentDescription = stringResource(description)
			)
		}
	}
}

@Composable
private fun Container(
	@StringRes category: Int,
	content: @Composable () -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(20.dp, 20.dp, 20.dp)
			.clip(MaterialTheme.shapes.large)
			.background(MaterialTheme.colorScheme.primaryContainer),
		contentAlignment = Alignment.Center
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 12.dp)
		) {
			Text(
				text = stringResource(category),
				modifier = Modifier
					.padding(20.dp, 14.dp)
					.alpha(0.7f),
				style = MaterialTheme.typography.titleMedium
			)
			content()
		}
	}
}

private fun optionsCategory(@StringRes category: Int) = OptionsMenu.values().filter { it.category == category }

fun String.getLanguage() = when (this) {
	LanguageOptions.English.local -> LanguageOptions.English.title
	LanguageOptions.French.local -> LanguageOptions.French.title
	else -> LanguageOptions.FollowSystem.title
}

private enum class OptionsMenu(
	@StringRes val option: Int,
	val icon: ImageVector,
	@StringRes val category: Int
) {
	Theme(R.string.theme, Icons.Rounded.WbIncandescent, R.string.display_category),
	SaveSearchHistory(R.string.save_history, Icons.Rounded.ManageHistory, R.string.search_history_category),
	ClearSearchHistory(R.string.clear_history, Icons.Rounded.ClearAll, R.string.search_history_category),
	HowToHandleMusicIntent(R.string.how_to_handle_music_intent, Icons.Rounded.MusicNote, R.string.preferences_category),
	Language(R.string.language, Icons.Rounded.Language, R.string.preferences_category),
	AboutUs(R.string.about_us, Icons.Rounded.ContactSupport, R.string.about_category),
	Support(R.string.support, Icons.Rounded.Coffee, R.string.about_category),
	ContactUs(R.string.contact_us, Icons.Rounded.ContactMail, R.string.about_category)
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
	OdiyoTheme {
		SettingsScreen(SettingsUiState(), {}, {}) {}
	}
}