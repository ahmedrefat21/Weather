package com.example.weatherforecast.prefernces

interface SharedPreferenceInterface {
    fun setTemperature(temperature: String)
    fun getTemperature(): String
    fun setWindSpeed(windSpeed: String)
    fun getWindSpeed(): String
    fun setLanguage(language: String)
    fun getLanguage(): String
    fun setUnit(unit: String)
    fun getUnit(): String
    fun setNotification(notification: String)
    fun getNotification(): String
    fun setMap(map: String)
    fun getMap(): String?
    fun setLatAndLon(lat: Double, lon: Double)
    fun setLatAndLonHome(lat: Double, lon: Double)
    fun getLatHome(): Double
    fun getLonHome(): Double
    fun getLat(): Double
    fun getLon(): Double
}