package com.example.weatherforecast.model

import WeatherResponse

sealed class ApiState {
    class Success(val weather: WeatherResponse) : ApiState()
    class Failure(val message: Throwable) : ApiState()
    object Loading : ApiState()
}