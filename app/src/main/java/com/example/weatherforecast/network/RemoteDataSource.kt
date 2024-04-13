package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse


interface RemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double, units: String, lang:String)
    : WeatherResponse
}