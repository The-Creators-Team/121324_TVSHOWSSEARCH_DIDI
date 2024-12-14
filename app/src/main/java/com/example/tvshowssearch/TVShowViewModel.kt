package com.example.tvshowssearch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TVShowViewModel(private val apiService: TVMazeApiService) : ViewModel() {

    fun searchShowByName(query: String) = liveData(Dispatchers.IO) {
        try {
            val response = apiService.searchShowByName(query)
            if (response.isSuccessful && response.body() != null) {
                val tvShow = response.body()
                tvShow?.let {
                    val daysSincePremiere = calculateDaysSincePremiere(it.premiered)
                    it.daysSincePremiere = daysSincePremiere

                }
                emit(tvShow)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
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
}

