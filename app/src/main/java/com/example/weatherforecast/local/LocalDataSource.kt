package com.example.weatherforecast.local


import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getCurrentWeather() : Flow<HomeEntity>
    suspend fun insertCurrentWeather(homeEntity: HomeEntity)
    suspend fun deleteCurrentWeather()

    fun getFavouriteCity() : Flow<List<FavoriteEntity>>
    suspend fun insertFavouriteCity(favoriteEntity : FavoriteEntity)
    suspend fun deleteFavouriteCity(id : Int)

    fun getAlert() : Flow<List<AlertEntity>>
    suspend fun insertAlert(alertEntity: AlertEntity)
    suspend fun deleteAlert(alertEntity: AlertEntity)
    fun getAlertById(id:String) : AlertEntity

}