package com.roland.android.domain.model

import com.roland.android.domain.util.SortOptions

data class Preference(
	val sortOption: SortOptions,
	val permissionStatus: Boolean
)
