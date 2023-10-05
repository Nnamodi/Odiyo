package com.roland.android.odiyo.ui.screens

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.ContactMail
import androidx.compose.material.icons.rounded.ContactSupport
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.ManageHistory
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.WbIncandescent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.states.SettingsUiState
import com.roland.android.odiyo.ui.components.AppBar
import com.roland.android.odiyo.ui.dialog.ThemeDialog
import com.roland.android.odiyo.ui.dialog.Themes
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.SettingsActions

@Composable
fun SettingsScreen(
	uiState: SettingsUiState,
	settingsAction: (SettingsActions) -> Unit,
	navigateUp: () -> Unit
) {
	val openThemeDialog = remember { mutableStateOf(false) }

	Scaffold(
		topBar = {
			AppBar(navigateUp = navigateUp, title = stringResource(R.string.settings))
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
			val aboutUsCategory by remember { mutableStateOf(optionsCategory(R.string.about_us_category)) }

			Container(displayCategory[0].category) {
				displayCategory.forEach {
					val optionIsTheme = it.name == OptionsMenu.Theme.name
					val subTitle = when {
						optionIsTheme && uiState.theme == Themes.System -> R.string.system
						optionIsTheme && uiState.theme == Themes.Dark -> R.string.dark_theme
						optionIsTheme && uiState.theme == Themes.Light -> R.string.light_theme
						else -> null
					}
					val action = { openThemeDialog.value = true }
					SettingsOption(
						icon = it.icon, option = it.option,
						subTitle = subTitle,
						subvertIcon = optionIsTheme
					) { action() }
				}
			}
			Container(searchHistoryCategory[0].category) {
				searchHistoryCategory.forEach {
					SettingsOption(icon = it.icon, option = it.option) {}
				}
			}
			Container(preferencesCategory[0].category) {
				preferencesCategory.forEach {
					val subTitle = when (it.name) {
						OptionsMenu.HowToHandleMusicIntent.name -> R.string.always_ask
						OptionsMenu.Language.name -> R.string.english
						else -> null
					}
					SettingsOption(
						icon = it.icon,
						option = it.option,
						subTitle = subTitle
					) {}
				}
			}
			Container(aboutUsCategory[0].category) {
				aboutUsCategory.forEach {
					SettingsOption(icon = it.icon, option = it.option) {}
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
}

@Composable
private fun SettingsOption(
	modifier: Modifier = Modifier,
	icon: ImageVector,
	@StringRes option: Int,
	@StringRes subTitle: Int? = null,
	subvertIcon: Boolean = false,
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
				.then(if (subvertIcon) Modifier.rotate(180f) else Modifier),
			imageVector = icon, contentDescription = null
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
	AboutUs(R.string.about_us, Icons.Rounded.ContactSupport, R.string.about_us_category),
	Support(R.string.support, Icons.Rounded.Coffee, R.string.about_us_category),
	ContactUs(R.string.contact_us, Icons.Rounded.ContactMail, R.string.about_us_category)
}

@Preview
@Composable
private fun SettingsScreenPreview() {
	OdiyoTheme {
		SettingsScreen(uiState = SettingsUiState(), settingsAction = {}) {}
	}
}