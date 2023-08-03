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
import androidx.compose.material3.*
import androidx.compose.material3.DismissDirection.EndToStart
import androidx.compose.material3.DismissDirection.StartToEnd
import androidx.compose.material3.DismissValue.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.ui.theme.color.dark_errorContainer
import com.roland.android.odiyo.ui.theme.color.dark_onTertiary
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableItem(
	componentColor: Color,
	dismissState: DismissState,
	defaultBackgroundColor: Color,
	content: @Composable () -> Unit
) {
	SwipeToDismiss(
		state = dismissState,
		background = {
			val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
			val color by animateColorAsState(
				when (dismissState.targetValue) {
					Default -> defaultBackgroundColor
					DismissedToEnd -> dark_errorContainer
					DismissedToStart -> dark_onTertiary
				}
			)
			val alignment = when (direction) {
				StartToEnd -> Alignment.CenterStart
				EndToStart -> Alignment.CenterEnd
			}
			val icon = when (direction) {
				StartToEnd -> Icons.Rounded.RemoveCircle
				EndToStart -> Icons.Rounded.AddCircle
			}
			val iconColor by animateColorAsState(
				if (dismissState.targetValue == Default) componentColor else Color.White
			)
			val scale by animateFloatAsState(
				if (dismissState.targetValue == Default) 0.75f else 1f
			)
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(color)
					.padding(horizontal = 20.dp),
				contentAlignment = alignment
			) {
				Icon(icon, null, Modifier.scale(scale), iconColor)
			}
		},
		dismissContent = { if (!dismissState.isDismissed(StartToEnd)) { content() } }
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSwipeToDismissState(
	index: Int,
	songUri: Uri,
	queueAction: (QueueItemActions) -> Unit
) = rememberDismissState(
	confirmValueChange = { dismissDirection ->
		val action: QueueItemActions? = when (dismissDirection) {
			DismissedToStart -> {
				QueueItemActions.DuplicateSong(QueueMediaItem(index, songUri))
			}
			DismissedToEnd -> {
				QueueItemActions.RemoveSong(QueueMediaItem(index, songUri))
			}
			else -> null
		}
		action?.let { queueAction(it) }
		dismissDirection == DismissedToEnd
	}
)