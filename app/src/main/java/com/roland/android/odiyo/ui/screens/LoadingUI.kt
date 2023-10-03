package com.roland.android.odiyo.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingUi(@StringRes loadingText: Int) {
	ElevatedCard(
		modifier = Modifier.fillMaxSize(),
		shape = RoundedCornerShape(0.dp),
		colors = CardDefaults.elevatedCardColors(
			MaterialTheme.colorScheme.background.copy(alpha = 0.75f)
		)
	) {
		Box(Modifier.fillMaxSize(), Alignment.Center) {
			Text(
				text = stringResource(loadingText),
				modifier = Modifier.padding(vertical = 2.dp),
				style = MaterialTheme.typography.titleMedium.copy(
					fontSize = 20.sp,
					fontWeight = FontWeight.Normal
				)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun LoadingListUi(modifier: Modifier = Modifier, isSongList: Boolean = true) {
	Column(modifier.verticalScroll(rememberScrollState())) {
		repeat(15) { ListItem(isSongList) }
		Spacer(Modifier.height(100.dp))
	}
}

@Preview
@Composable
fun LoadingRowUi(modifier: Modifier = Modifier) {
	Row(modifier.horizontalScroll(rememberScrollState())) {
		Spacer(Modifier.width(16.dp))
		repeat(10) { RowItem() }
		Spacer(Modifier.width(16.dp))
	}
}

@Preview
@Composable
private fun ListItem(isSongList: Boolean = true) {
	val itemColor = rememberAnimatedShimmerBrush()

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Spacer(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(50.dp)
				.clip(MaterialTheme.shapes.large)
				.background(itemColor)
		)
		Column {
			Spacer(
				modifier = Modifier
					.clip(MaterialTheme.shapes.small)
					.size(width = 160.dp, height = 16.dp)
					.background(itemColor)
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.wrapContentHeight()
					.padding(top = 4.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Spacer(
					modifier = Modifier
						.clip(MaterialTheme.shapes.small)
						.size(width = 80.dp, height = 12.dp)
						.background(itemColor)
				)
				if (isSongList) {
					Spacer(
						modifier = Modifier
							.clip(MaterialTheme.shapes.small)
							.size(width = 40.dp, height = 12.dp)
							.background(itemColor)
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun RowItem() {
	val imageSize = LocalConfiguration.current.screenWidthDp / 2.5
	val itemColor = rememberAnimatedShimmerBrush()

	Column(
		modifier = Modifier
			.width(imageSize.dp + 16.dp)
			.clip(MaterialTheme.shapes.large)
			.padding(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Spacer(
			modifier = Modifier
				.size(imageSize.dp)
				.clip(MaterialTheme.shapes.large)
				.background(itemColor),
		)
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 10.dp)
		) {
			Spacer(
				modifier = Modifier
					.clip(MaterialTheme.shapes.small)
					.size(width = 100.dp, height = 16.dp)
					.background(itemColor)
			)
			Spacer(
				modifier = Modifier
					.padding(top = 4.dp)
					.clip(MaterialTheme.shapes.small)
					.size(width = 50.dp, height = 12.dp)
					.background(itemColor)
			)
		}
	}
}

@Composable
private fun rememberAnimatedShimmerBrush(): Brush {
	val shimmerColors = listOf(
		Color.LightGray.copy(alpha = 0.6f),
		Color.LightGray.copy(alpha = 0.2f),
		Color.LightGray.copy(alpha = 0.6f)
	)

	val transition = rememberInfiniteTransition()
	val translateAnim = transition.animateFloat(
		initialValue = 0f,
		targetValue = 1000f,
		animationSpec = infiniteRepeatable(
			animation = tween(
				durationMillis = 1000,
				easing = FastOutLinearInEasing
			),
			repeatMode = RepeatMode.Reverse
		)
	)

	return Brush.linearGradient(
		colors = shimmerColors,
		start = Offset.Zero,
		end = Offset(x = translateAnim.value, y = translateAnim.value)
	)
}