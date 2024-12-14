package com.example.tvshowssearch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshowssearch.data.api.TVMazeApiService

class ShowViewModelFactory(private val apiService: TVMazeApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TVShowViewModel(apiService) as T
    }
}
