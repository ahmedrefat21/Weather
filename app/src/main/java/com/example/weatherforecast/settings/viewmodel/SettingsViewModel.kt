package com.example.weatherforecast.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.prefernces.SharedPreference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val sharedPreference: SharedPreference)
    : ViewModel(){


        val language = MutableSharedFlow<String>(replay = 1)
    fun setLanguage(language: String){
        sharedPreference.setLanguage(language)
    }
    fun getLanguage() = sharedPreference.getLanguage()


    fun setTemperatureUnit(unit:String){
        sharedPreference.setUnit(unit)
    }

    fun getTemperatureUnit() = sharedPreference.getTemperature()


    fun setNotification(notification: String){
        sharedPreference.setNotification(notification)
    }

    fun getNotification() = sharedPreference.getNotification()


    fun setWindSpeed(windSpeed: String){
        sharedPreference.setWindSpeed(windSpeed)
    }

    fun getWindSpeed() = sharedPreference.getWindSpeed()

    fun setTemperature(temperature: String) {
        sharedPreference.setTemperature(temperature)
    }

    fun changeLanguageShared(lang: String){
        viewModelScope.launch {
           language.emit(lang)
        }
    }







}