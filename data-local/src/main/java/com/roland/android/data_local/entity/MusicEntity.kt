package com.roland.android.data_local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class MusicEntity(
	@PrimaryKey(autoGenerate = true)
	val generatedId: Int = 0,
	val id: Long,
	val uri: Uri,
	val name: String,
	val title: String,
	val artist: String,
	val album: String,
	val path: String,
	val addedOn: String,
	val duration: String,
	val size: String,
	val favorite: Boolean = false,
	val lastPlayed: Date = Date(0),
	val timesPlayed: Int = 0
)