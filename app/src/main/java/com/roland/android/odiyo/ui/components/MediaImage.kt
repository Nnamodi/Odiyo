package com.roland.android.odiyo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun MediaImage(
	modifier: Modifier = Modifier,
	artwork: Any?,
	descriptionRes: Int = R.string.music_art_desc,
	placeholderRes: Int = R.drawable.default_art
) {
	AsyncImage(
		model = artwork,
		contentDescription = stringResource(descriptionRes),
		contentScale = ContentScale.Crop,
		placeholder = painterResource(placeholderRes),
		modifier = modifier
			.then(Modifier.clip(MaterialTheme.shapes.large))
	)
}