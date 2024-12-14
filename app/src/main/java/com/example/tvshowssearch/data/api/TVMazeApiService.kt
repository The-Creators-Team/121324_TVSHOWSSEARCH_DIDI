package com.example.tvshowssearch.data.api

import com.example.tvshowssearch.data.model.TVShow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TVMazeApiService {
    @GET("singlesearch/shows")
    suspend fun searchShowByName(@Query("q") query: String): Response<TVShow>
}
