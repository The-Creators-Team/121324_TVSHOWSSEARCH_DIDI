package com.example.tvshowssearch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TVShowDao {

    @Insert
    suspend fun insert(tvShow: TVShowEntity)

    @Query("SELECT * FROM tv_shows WHERE name LIKE :query LIMIT 1")
    suspend fun getShowByName(query: String): TVShowEntity?
}
