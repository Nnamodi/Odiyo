package com.roland.android.odiyo.viewmodel

import android.content.ContentResolver
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.odiyo.data.MediaSource
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
class OdiyoViewModel(
	resolver: ContentResolver
) : ViewModel() {
	private val mediaSource = MediaSource(viewModelScope, resolver)
	var songs by mutableStateOf<List<Music>>(emptyList())

	init {
		viewModelScope.launch {
			mediaSource.media().collect {
				songs = it
			}
		}
	}
}