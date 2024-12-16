package com.roland.android.data_local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class PlaylistEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val name: String,
	val songs: List<Uri>,
	val dateCreated: Date = Date(),
	val dateModified: Date = Date()
) {
	fun numOfSongs() = songs.size - 1
}