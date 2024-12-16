package com.example.tvshowssearch.ui.viewmodel

import android.content.Context
import androidx.compose.ui.window.application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.tvshowssearch.data.api.RetrofitInstance
import com.example.tvshowssearch.data.api.TVMazeApiService
import com.example.tvshowssearch.data.local.AppDatabase
import com.example.tvshowssearch.data.local.TVShowDao
import com.example.tvshowssearch.data.local.TVShowEntity
import com.example.tvshowssearch.data.model.TVShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TVShowViewModel(private val context: Context) : ViewModel() {

    private val tvShowDao: TVShowDao
    private val tvShowDatabase: AppDatabase

    init {

        tvShowDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "tv_show_database")
            .build()
        tvShowDao = tvShowDatabase.tvShowDao()
    }

    private val _tvShow = MutableLiveData<TVShow?>()
    val tvShow: LiveData<TVShow?> get() = _tvShow

    fun searchTVShowByName(showName: String) {
        viewModelScope.launch {

            val cachedShow = tvShowDao.getShowByName(showName)
            if (cachedShow != null) {
                _tvShow.postValue(cachedShow.toTVShow())
            } else {

                fetchTVShowFromApi(showName)
            }
        }
    }

    private suspend fun fetchTVShowFromApi(showName: String) {

        val response = RetrofitInstance.retrofitService.searchShowByName(showName)
        if (response.isSuccessful && response.body() != null) {
            val show = response.body()!!
            val premieredDaysAgo = calculateDaysSincePremiere(show.premiered)
            val tvShow = TVShowEntity(
                id = show.id,
                name = show.name,
                imageUrl = show.image?.medium ?: "https://t4.ftcdn.net/jpg/04/73/25/49/360_F_473254957_bxG9yf4ly7OBO5I0O5KABlN930GwaMQz.jpg",
                premiered = show.premiered,
                premieredDaysAgo = premieredDaysAgo
            )

            tvShowDao.insert(tvShow)
            _tvShow.postValue(tvShow.toTVShow())
        } else {
            _tvShow.postValue(null)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TVShowViewModel(context, ) as T
        }
    }

    private fun calculateDaysSincePremiere(premiereDate: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val premiereDateParsed = dateFormat.parse(premiereDate)
            val currentDate = Date()

            val differenceInMillis = currentDate.time - premiereDateParsed.time
            val differenceInDays = (differenceInMillis / (1000 * 60 * 60 * 24)).toLong()

            abs(differenceInDays)
        } catch (e: Exception) {
            0L
        }
    }

    fun TVShowEntity.toTVShow(): TVShow {
        return TVShow(
            id = this.id,
            name = this.name,
            url = this.imageUrl,
            image = null,
            premiered = this.premiered,
            daysSincePremiere = this.premieredDaysAgo
        )
    }
}







