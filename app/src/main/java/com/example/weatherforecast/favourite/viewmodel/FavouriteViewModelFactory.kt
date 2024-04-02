package com.example.weatherforecast.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.Repository

class FavouriteViewModelFactory (private val repo : Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(repo) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}