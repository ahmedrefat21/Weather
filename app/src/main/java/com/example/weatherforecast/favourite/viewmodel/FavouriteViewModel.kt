package com.example.weatherforecast.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.FavouriteState
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repo : Repository) : ViewModel() {
    var favorite = MutableStateFlow<FavouriteState>(FavouriteState.Loading)
    var weather = MutableStateFlow<ApiState>(ApiState.Loading)

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(latitude, longitude, units, lang).collect{
                weather.value = ApiState.Success(it)
            }
        }
    }



    fun getFavoriteWeather(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getFavoriteCity()
                .catch {
                        e -> favorite.value = FavouriteState.Failure(e)
                }.collect {
                        data -> favorite.value = FavouriteState.Success(data)
                }
        }
    }


    fun deleteFavoriteWeather(id : Int){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteFavoriteCity(id)
        }
    }


}