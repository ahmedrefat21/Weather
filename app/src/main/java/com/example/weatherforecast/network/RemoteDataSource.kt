package com.example.weatherforecast.network

import WeatherResponse

interface RemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double, lon: Double, units: String, lang:String) : WeatherResponse
}