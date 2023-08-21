package com.roland.android.odiyo.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.theme.color.CustomColors

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

@Composable
fun NowPlayingIconButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	toggled: Boolean = false,
	color: Color,
	content: @Composable () -> Unit
) {
	Box(
		modifier = modifier
			.minimumInteractiveComponentSize()
			.size(40.dp)
			.clip(CircleShape)
			.background(color = Color.Transparent)
			.clickable(
				onClick = onClick, enabled = enabled, role = Role.Button,
				interactionSource = remember { MutableInteractionSource() },
				indication = rememberRipple(bounded = false, radius = 30.dp, color = CustomColors.rippleColor(color))
			),
		contentAlignment = Alignment.Center
	) {
		val contentColor = CustomColors.componentColor(color, toggled)
		CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
	}
}