package com.roland.android.odiyo.ui.components

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
@Composable
fun MediaImage(
	modifier: Modifier = Modifier,
	artwork: Bitmap?,
	placeholderRes: Int = R.drawable.default_art
) {
	AsyncImage(
		model = artwork,
		contentDescription = null,
		contentScale = ContentScale.Crop,
		placeholder = painterResource(placeholderRes),
		modifier = modifier
			.then(Modifier.clip(MaterialTheme.shapes.large))
	)
}