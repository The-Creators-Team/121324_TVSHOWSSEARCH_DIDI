package com.example.tvshowssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShowViewModelFactory(private val apiService: TVMazeApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TVShowViewModel(apiService) as T
    }
}
