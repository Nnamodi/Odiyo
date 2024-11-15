package com.roland.android.odiyo.ui.components

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.roland.android.domain.model.QueueMediaItem
import com.roland.android.odiyo.ui.theme.color.dark_errorContainer
import com.roland.android.odiyo.ui.theme.color.dark_onTertiary
import com.roland.android.odiyo.util.actions.QueueItemActions

@Composable
fun SwipeableItem(
	componentColor: Color,
	dismissState: SwipeToDismissBoxState,
	defaultBackgroundColor: Color,
	content: @Composable () -> Unit
) {
	SwipeToDismissBox(
		state = dismissState,
		backgroundContent = {
			val direction = dismissState.dismissDirection
			val color by animateColorAsState(
				targetValue = when (dismissState.targetValue) {
					Settled -> defaultBackgroundColor
					StartToEnd -> dark_errorContainer
					EndToStart -> dark_onTertiary
				},
				label = "content color"
			)
			val alignment = when (direction) {
				StartToEnd -> Alignment.CenterStart
				EndToStart -> Alignment.CenterEnd
				Settled -> Alignment.Center
			}
			val icon = when (direction) {
				StartToEnd -> Icons.Rounded.RemoveCircle
				EndToStart -> Icons.Rounded.AddCircle
				Settled -> null
			}
			val iconColor by animateColorAsState(
				targetValue = if (dismissState.targetValue == Settled) componentColor else Color.White,
				label = "icon color"
			)
			val scale by animateFloatAsState(
				targetValue = if (dismissState.targetValue == Settled) 0.75f else 1f,
				label = "scale value"
			)
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(color)
					.padding(horizontal = 20.dp),
				contentAlignment = alignment
			) {
				icon?.let {
					Icon(
						imageVector = it,
						contentDescription = null,
						modifier = Modifier.scale(scale),
						tint = iconColor
					)
				}
			}
		}
	) { if (dismissState.currentValue != StartToEnd) { content() } }
}

@Composable
fun rememberSwipeToDismissState(
	index: Int,
	songUri: Uri,
	queueAction: (QueueItemActions) -> Unit
) = rememberSwipeToDismissBoxState(
	confirmValueChange = { dismissDirection ->
		val action: QueueItemActions? = when (dismissDirection) {
			EndToStart -> {
				QueueItemActions.DuplicateSong(QueueMediaItem(index, songUri))
			}
			StartToEnd -> {
				QueueItemActions.RemoveSong(QueueMediaItem(index, songUri))
			}
			else -> null
		}
		action?.let { queueAction(it) }
		dismissDirection == EndToStart
	}
)