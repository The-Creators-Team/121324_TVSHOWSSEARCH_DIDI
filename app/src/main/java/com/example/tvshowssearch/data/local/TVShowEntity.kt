package com.example.tvshowssearch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_shows")
data class TVShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val premiered: String,
    val imageUrl: String,
    val premieredDaysAgo: Long
)

