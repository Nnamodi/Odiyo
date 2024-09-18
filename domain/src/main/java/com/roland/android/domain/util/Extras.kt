package com.roland.android.domain.util

import com.roland.android.domain.R

enum class SortOptions(val title: Int) {
	NameAZ(R.string.name_a_z),
	NameZA(R.string.name_z_a),
	NewestFirst(R.string.newest_first),
	OldestFirst(R.string.oldest_first)
}
