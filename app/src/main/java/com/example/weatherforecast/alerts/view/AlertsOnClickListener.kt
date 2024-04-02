package com.example.weatherforecast.alerts.view

import com.example.weatherforecast.model.entity.AlertEntity


interface AlertsOnClickListener {

    fun deleteAlertItem(alertEntity: AlertEntity)

}