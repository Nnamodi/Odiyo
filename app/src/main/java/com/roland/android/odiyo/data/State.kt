package com.roland.android.odiyo.data

sealed class State<out T : Any> {

	data object Loading : State<Nothing>()

	data class Success<out T : Any>(val data: T) : State<T>()

}
