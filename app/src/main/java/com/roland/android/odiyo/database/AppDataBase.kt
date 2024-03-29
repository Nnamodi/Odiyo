package com.roland.android.odiyo.database

import androidx.room.*
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist

@Database(
	entities = [Music::class, Playlist::class],
	version = 5,
	autoMigrations = [AutoMigration(from = 4, to = 5)]
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun musicDao(): MusicDao

	abstract fun playlistDao(): PlaylistDao
}