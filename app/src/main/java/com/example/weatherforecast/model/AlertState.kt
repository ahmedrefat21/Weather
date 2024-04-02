package com.example.weatherforecast.model

import com.example.weatherforecast.model.entity.AlertEntity


sealed class AlertState {

    class Success(val alertEntity: List<AlertEntity>) : AlertState()
    class Failure(val message: Throwable) : AlertState()
    object Loading : AlertState()

}