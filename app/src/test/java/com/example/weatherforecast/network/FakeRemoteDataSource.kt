package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse

class FakeRemoteDataSource(private var weatherResponse: WeatherResponse): RemoteDataSource{
    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        return weatherResponse
    }
}