package com.roland.android.odiyo.data

import kotlinx.coroutines.flow.MutableStateFlow

sealed class State<out T : Any> {

	data object Loading : State<Nothing>()

	data class Success<out T : Any>(val data: T) : State<T>()

}

val readStoragePermissionGranted = MutableStateFlow(false)
