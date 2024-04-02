package com.example.weatherforecast.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: Repository)
    : ViewModel() {

    val alert = MutableStateFlow<AlertState>(AlertState.Loading)
    val weather = MutableStateFlow<ApiState>(ApiState.Loading)
    val isEmptyData = MutableSharedFlow<Boolean>()

    init {
        getAlert()
    }

    fun getWeather(lat: Double, lon: Double, units: String, lang:String) {
        viewModelScope.launch{
            repo.getWeather(lat,lon,units,lang).catch {
                    e-> weather.value = ApiState.Failure(e)
            }.collect {
                    data -> weather.value = ApiState.Success(data)
            }
        }
    }

    fun insertAlert(alertEntity: AlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlert(alertEntity)
        }
    }

    fun deleteAlert(alertEntity: AlertEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlert(alertEntity)
        }
    }

    fun getAlert(){
        viewModelScope.launch(Dispatchers.IO)  {
            repo.getAlert().catch {
                    e-> alert.value = AlertState.Failure(e)
            }.collect{
                    data -> alert.value = AlertState.Success(data)
            }
        }
    }


    fun showToast(){
        viewModelScope.launch(Dispatchers.IO) {
            isEmptyData.emit(true)
        }
    }

    fun hideToast(){
        isEmptyData.tryEmit(false)
    }




}