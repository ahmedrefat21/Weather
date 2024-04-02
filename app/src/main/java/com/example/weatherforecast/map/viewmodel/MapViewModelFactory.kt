package com.example.weatherforecast.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.Repository

class MapViewModelFactory (private val repo : Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(repo) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}