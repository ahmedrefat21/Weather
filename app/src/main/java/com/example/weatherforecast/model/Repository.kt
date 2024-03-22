package com.example.weatherforecast.model

import WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String) : Flow<WeatherResponse>
}