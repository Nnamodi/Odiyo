package com.roland.android.odiyo.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roland.android.odiyo.model.Music

@RequiresApi(Build.VERSION_CODES.Q)
@Database(entities = [Music::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MusicDatabase : RoomDatabase() {
	abstract fun musicDao(): MusicDao
}