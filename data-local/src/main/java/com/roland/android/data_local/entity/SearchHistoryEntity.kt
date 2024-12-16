package com.roland.android.data_local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchQueryEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val query: String
)
