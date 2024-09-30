package com.roland.android.data_local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roland.android.data_local.entity.MusicEntity
import com.roland.android.data_local.entity.PlaylistEntity
import com.roland.android.data_local.entity.SearchQueryEntity

@Database(
	entities = [
		MusicEntity::class,
		PlaylistEntity::class,
		SearchQueryEntity::class
	],
	version = 6,
	autoMigrations = [AutoMigration(from = 5, to = 6)]
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun musicDao(): MusicDao

	abstract fun playlistDao(): PlaylistDao

	abstract fun searchDao(): SearchDao
}