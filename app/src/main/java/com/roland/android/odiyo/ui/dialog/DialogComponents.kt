package com.roland.android.odiyo.ui.dialog

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun CustomInputText(text: String) {
	Text(text = text, fontSize = 18.sp)
}

@Composable
fun DialogButtonText(text: String) {
	Text(text = text, fontSize = 18.sp)
}