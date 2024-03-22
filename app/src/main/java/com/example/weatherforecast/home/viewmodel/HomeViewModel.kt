package com.example.weatherforecast.home.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val repo: Repository): ViewModel() {

    private val state = MutableStateFlow<ApiState>(ApiState.Loading)
    val weather= state

    fun getWeather(lat: Double, lon: Double, units: String, lang:String) {
        viewModelScope.launch{
            repo.getWeather(lat,lon,units,lang).catch {
                    e-> weather.value = ApiState.Failure(e)
            }.collect {
                    data -> weather.value = ApiState.Success(data)
            }
        }
    }



}