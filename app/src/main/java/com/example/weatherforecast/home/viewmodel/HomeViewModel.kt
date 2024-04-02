package com.example.weatherforecast.home.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.CurrentState
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel (private val repo: Repository): ViewModel() {

    private val state = MutableStateFlow<ApiState>(ApiState.Loading)
    val weather= state
    var current = MutableStateFlow<CurrentState>(CurrentState.Loading)

    fun getWeather(lat: Double, lon: Double, units: String, lang:String) {
        viewModelScope.launch{
            repo.getWeather(lat,lon,units,lang).catch {
                    e-> weather.value = ApiState.Failure(e)
            }.collect {
                    data -> weather.value = ApiState.Success(data)
            }
        }
    }

    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeather()
                .catch {
                        e -> current.value = CurrentState.Failure(e)
                }?.collectLatest {
                        data -> current.value = CurrentState.Success(data)
                }
        }
    }

    fun insertCurrentWeather(homeEntity: HomeEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeather(homeEntity)
        }
    }

    fun deleteCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCurrentWeather()
        }
    }







}