package com.example.weatherforecast.model


import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String) : Flow<WeatherResponse>

    fun getCurrentWeather() : Flow<HomeEntity>
    suspend fun insertCurrentWeather(homeEntity: HomeEntity)
    suspend fun deleteCurrentWeather()


    fun getFavoriteCity() : Flow<List<FavoriteEntity>>
    suspend fun insertFavoriteCity(favoriteEntity : FavoriteEntity)
    suspend fun deleteFavoriteCity(id : Int)

    fun getAlert() : Flow<List<AlertEntity>>
    suspend fun insertAlert(alertEntity: AlertEntity)
    suspend fun deleteAlert(alertEntity: AlertEntity)
    fun getAlertById(id:String): AlertEntity

    suspend fun getWeatherAlert (lat: Double,lon: Double,units: String,lang:String) : WeatherResponse
}