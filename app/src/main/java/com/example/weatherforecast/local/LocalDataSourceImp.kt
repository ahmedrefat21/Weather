package com.example.weatherforecast.local


import android.content.Context
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImp(context : Context) : LocalDataSource {


    private val dao : WeatherDao by lazy {
        val db : WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getWeather()
    }
    override fun getCurrentWeather(): Flow<HomeEntity> {
        return dao.getCurrentWeather()
    }
    override suspend fun insertCurrentWeather(homeEntity: HomeEntity) {
        dao.insertCurrentWeather(homeEntity)
    }
    override suspend fun deleteCurrentWeather() {
        dao.deleteCurrentWeather()
    }


    override fun getFavouriteCity(): Flow<List<FavoriteEntity>> {
        return dao.getAllFavouriteCities()
    }

    override suspend fun insertFavouriteCity(favoriteEntity: FavoriteEntity) {
        dao.insertFavouriteCity(favoriteEntity)
    }

    override suspend fun deleteFavouriteCity(id : Int) {
        dao.deleteFavouriteCity(id)
    }

    override fun getAlert(): Flow<List<AlertEntity>> = dao.getAllAlerts()

    override suspend fun insertAlert(alertEntity: AlertEntity) {
        dao.insertAlert(alertEntity)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        dao.deleteAlert(alertEntity)
    }
    override fun getAlertById(id: String): AlertEntity {
        return dao.getAlertById(id)

    }

}