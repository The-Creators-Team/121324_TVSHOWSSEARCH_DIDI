package com.example.tvshowssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TVShowViewModel(private val apiService: TVMazeApiService) : ViewModel() {

    // LiveData for holding the result
    fun searchShowByName(query: String) = liveData(Dispatchers.IO) {
        try {
            val response = apiService.searchShowByName(query)
            if (response.isSuccessful && response.body() != null) {
                val tvShow = response.body()
                tvShow?.let {
                    // Calculate the number of days since the show premiered
                    val daysSincePremiere = calculateDaysSincePremiere(it.premiered)
                    it.daysSincePremiere = daysSincePremiere
                }
                emit(tvShow)
            } else {
                emit(null) // No data found
            }
        } catch (e: Exception) {
            emit(null) // Handle errors (e.g. network failure)
        }
    }

    private fun calculateDaysSincePremiere(premiereDate: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val premiereDateParsed = dateFormat.parse(premiereDate)
            val currentDate = Date()

            val differenceInMillis = currentDate.time - premiereDateParsed.time
            val differenceInDays = (differenceInMillis / (1000 * 60 * 60 * 24)).toLong()

            abs(differenceInDays) // Return positive value
        } catch (e: Exception) {
            0L  // If the date is invalid or parsing fails, return 0 days
        }
    }
}

