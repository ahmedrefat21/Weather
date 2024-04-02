package com.example.weatherforecast.model

import com.example.weatherforecast.model.entity.HomeEntity

sealed class CurrentState {

    class Success(val homeEntity: HomeEntity) : CurrentState()
    class Failure(val message: Throwable) : CurrentState()
    object Loading : CurrentState()
}