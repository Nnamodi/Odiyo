package com.roland.android.odiyo.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist

@RequiresApi(Build.VERSION_CODES.Q)
@Database(
	entities = [Music::class, Playlist::class],
	version = 4,
	autoMigrations = [
		AutoMigration(
			from = 3, to = 4,
			spec = AppDatabase.MigrationSpec::class
		)
	]
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun musicDao(): MusicDao

	abstract fun playlistDao(): PlaylistDao

	@DeleteColumn(tableName = "Playlist", columnName = "numSongs")
	class MigrationSpec : AutoMigrationSpec
}