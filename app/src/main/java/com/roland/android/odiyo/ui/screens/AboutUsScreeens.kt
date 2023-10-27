package com.roland.android.odiyo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.AppBar
import com.roland.android.odiyo.ui.navigation.ABOUT_US
import com.roland.android.odiyo.ui.navigation.SUPPORT
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.light_onPrimaryContainer

@Composable
fun AboutUsScreen(screenToShow: String, navigateUp: () -> Unit) {
	val aboutUs by remember { derivedStateOf { screenToShow == ABOUT_US } }

	Scaffold(
		topBar = {
			AppBar(
				navigateUp = navigateUp,
				title = stringResource(if (aboutUs) R.string.about_us else R.string.support)
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.padding(horizontal = 30.dp)
		) {
			if (aboutUs) {
				DeveloperInfoScreen()
			} else {
				SupportScreen()
			}
		}
	}
}

@Composable
private fun DeveloperInfoScreen() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(Modifier.height(20.dp))
		Row(
			modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Image(
				painter = painterResource(R.drawable.app_icon),
				contentDescription = stringResource(R.string.app_icon_desc),
				modifier = Modifier
					.size(40.dp)
					.clip(MaterialTheme.shapes.large)
					.background(light_onPrimaryContainer),
				contentScale = ContentScale.Fit
			)
			Text(
				text = stringResource(R.string.app_name),
				modifier = Modifier.padding(start = 10.dp),
				style = MaterialTheme.typography.headlineMedium
			)
		}
		Text(text = stringResource(R.string.developer_story))
		Spacer(Modifier.height(80.dp))
	}
}

@Composable
private fun SupportScreen() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(Modifier.height(20.dp))
		Text(text = stringResource(R.string.support_info))
		Box(
			modifier = Modifier
				.padding(start = 20.dp, top = 40.dp, end = 20.dp)
				.clip(MaterialTheme.shapes.large)
				.background(MaterialTheme.colorScheme.primaryContainer)
		) {
			Column(
				modifier = Modifier.padding(20.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = stringResource(R.string.wallet_address_title),
					modifier = Modifier.padding(bottom = 10.dp),
					style = MaterialTheme.typography.titleMedium
				)
				Text(text = stringResource(R.string.wallet_address_info))
			}
		}
		Spacer(Modifier.height(80.dp))
	}
}

@Preview
@Composable
private fun DeveloperInfoScreenPreview() {
	OdiyoTheme {
		AboutUsScreen(screenToShow = ABOUT_US) {}
	}
}

@Preview
@Composable
private fun SupportScreenPreview() {
	OdiyoTheme {
		AboutUsScreen(screenToShow = SUPPORT) {}
	}
}