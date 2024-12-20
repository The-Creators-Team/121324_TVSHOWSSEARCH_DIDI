package com.example.tvshowssearch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TVShowEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tvShowDao(): TVShowDao
}
