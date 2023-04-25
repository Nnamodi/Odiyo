package com.roland.android.odiyo.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.theme.OdiyoTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
	song: Music?,
	isPlaying: Boolean,
	playPause: (Uri) -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
	navigateUp: () -> Unit,
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				navigationIcon = {
					IconButton(onClick = navigateUp) {
						Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
					}
				},
				title = {},
				colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
			)
		}
	) {
		Column(
			modifier = Modifier.padding(horizontal = 40.dp, vertical = 40.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(song)

			MediaControls(
				isPlaying = isPlaying,
				playPause = { song?.uri?.let { playPause(it) } },
				seekTo = seekTo
			)
		}
	}
}

@Composable
private fun MediaDescription(song: Music?) {
	Image(
		imageVector = Icons.Default.Headset,
		contentDescription = "music photo",
		modifier = Modifier
			.fillMaxHeight(0.5f)
			.fillMaxWidth()
			.padding(vertical = 40.dp)
			.then(Modifier.background(color = Color.Gray))
	)
	Column(Modifier.fillMaxWidth()) {
		Text(
			text = song?.title ?: "",
			style = MaterialTheme.typography.headlineMedium,
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
			fontWeight = FontWeight.Medium
		)
		Text(
			text = song?.artist ?: "",
			fontSize = TextUnit(18f, TextUnitType.Sp),
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
		)
	}
}

@Composable
private fun MediaControls(
	isPlaying: Boolean,
	playPause: () -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.padding(top = 70.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = Icons.Default.SkipPrevious,
			contentDescription = "seek to previous",
			modifier = Modifier
				.clickable { seekTo(true, false) }
				.size(50.dp)
		)
		Icon(
			imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
			contentDescription = "pause",
			modifier = Modifier
				.clickable { playPause() }
				.size(70.dp)
		)
		Icon(
			imageVector = Icons.Default.SkipNext,
			contentDescription = "seek to next",
			modifier = Modifier
				.clickable { seekTo(false, true) }
				.size(50.dp)
		)
	}
}

@Preview
@Composable
fun NowPlayingPreview() {
	OdiyoTheme {
		NowPlayingScreen(
			song = previewData[2],
			isPlaying = false,
			playPause = {},
			seekTo = { _, _ -> },
			navigateUp = {}
		)
	}
}