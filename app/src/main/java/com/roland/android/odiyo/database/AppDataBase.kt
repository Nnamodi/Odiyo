package com.roland.android.odiyo.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist

@RequiresApi(Build.VERSION_CODES.Q)
@Database(
	entities = [Music::class, Playlist::class],
	version = 2,
	autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun musicDao(): MusicDao

	abstract fun playlistDao(): PlaylistDao
}