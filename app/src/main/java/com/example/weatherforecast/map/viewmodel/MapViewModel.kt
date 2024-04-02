package com.example.weatherforecast.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MapViewModel( private val repo : Repository) : ViewModel() {

    var weather = MutableStateFlow<ApiState>(ApiState.Loading)
    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(latitude, longitude, units, lang).collect{
                weather.value = ApiState.Success(it)
            }
        }
    }

    fun insertFavoriteCity(favoriteEntity: FavoriteEntity){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertFavoriteCity(favoriteEntity)
        }
    }


}