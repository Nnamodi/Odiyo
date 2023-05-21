package com.roland.android.odiyo.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CustomInputText(text: String, modifier: Modifier = Modifier) {
	Text(modifier = modifier, text = text, fontSize = 18.sp)
}

@Composable
fun DialogButtonText(text: String, modifier: Modifier = Modifier) {
	Text(modifier = modifier, text = text, fontSize = 18.sp)
}

@Composable
fun SongDetailText(text: String, modifier: Modifier = Modifier) {
	Text(modifier = modifier, text = text, fontWeight = FontWeight.Bold)
}