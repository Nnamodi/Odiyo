package com.roland.android.odiyo.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun NowPlayingScreen(
	song: Music?,
	isPlaying: Boolean,
	progress: Float,
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
						Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
					}
				},
				title = {},
				colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
			)
		}
	) {
		Column(
			modifier = Modifier.padding(horizontal = 20.dp, vertical = 40.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(song)

			MediaControls(
				isPlaying = isPlaying,
				progress = if (progress < 1f) progress else 0.2f,
				totalDuration = song?.duration ?: "00:00",
				playPause = { song?.uri?.let { playPause(it) } },
				seekTo = seekTo
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@UnstableApi
@Composable
private fun MediaDescription(song: Music?) {
	AsyncImage(
		model = song?.getArtwork(),
		contentDescription = "media thumbnail",
		placeholder = painterResource(R.drawable.default_art),
		modifier = Modifier
			.fillMaxHeight(0.5f)
			.fillMaxWidth()
			.padding(top = 40.dp, bottom = 20.dp)
	)
	Column(Modifier.fillMaxWidth()) {
		Text(
			text = song?.title ?: "",
			modifier = Modifier.basicMarquee(),
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
	progress: Float,
	timeElapsed: String = "1:41",
	totalDuration: String,
	playPause: () -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
) {
	var slideValue by remember { mutableStateOf(progress) }

	Slider(
		value = slideValue,
		onValueChange = { slideValue = it },
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 25.dp)
	)
	Row {
		Text(timeElapsed)
		Spacer(Modifier.weight(1f))
		Text(totalDuration)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.padding(top = 50.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			onClick = { seekTo(true, false) },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipPrevious,
				contentDescription = "seek to previous",
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		IconButton(
			onClick = { playPause() },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
				contentDescription = if (isPlaying) "pause" else "play",
				modifier = Modifier.fillMaxSize()
			)
		}
		IconButton(
			onClick = { seekTo(false, true) },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipNext,
				contentDescription = "seek to next",
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun NowPlayingPreview() {
	OdiyoTheme {
		NowPlayingScreen(
			song = previewData[2],
			isPlaying = false,
			progress = 0.25f,
			playPause = {},
			seekTo = { _, _ -> },
			navigateUp = {}
		)
	}
}