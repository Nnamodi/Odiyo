package com.roland.android.odiyo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roland.android.odiyo.data.State

@Composable
fun <T : Any> CommonScreen(
	state: State<T>,
	modifier: Modifier = Modifier,
	loadingScreen: @Composable () -> Unit,
	successScreen: @Composable (T) -> Unit
) {
	Box(modifier) {
		when (state) {
			State.Loading -> loadingScreen()
			is State.Success -> successScreen(state.data)
		}
	}
}