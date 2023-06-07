package com.roland.android.odiyo.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Playlist(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	var name: String,
	var songs: List<Uri>,
	@ColumnInfo(defaultValue = 0.toString())
	var numSongs: Int = 0,
	var dateCreated: Date = Date(),
	var dateModified: Date = Date()
)