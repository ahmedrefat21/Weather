package com.example.weatherforecast.local

import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(
    private var homeEntities : MutableList<HomeEntity> = mutableListOf(),
    private var favoriteEntities : MutableList<FavoriteEntity> = mutableListOf(),
    private var alertEntities : MutableList<AlertEntity> = mutableListOf()
):LocalDataSource {
    override fun getCurrentWeather(): Flow<HomeEntity> = homeEntities.let {
        return@let it.asFlow()
    }


    override suspend fun insertCurrentWeather(homeEntity: HomeEntity) {
        homeEntities.add(homeEntity)
    }

    override suspend fun deleteCurrentWeather() {
        homeEntities.clear()
    }

    override fun getFavouriteCity(): Flow<List<FavoriteEntity>>  = flow{
        emit(favoriteEntities)
    }

    override suspend fun insertFavouriteCity(favoriteEntity: FavoriteEntity) {
        favoriteEntities.add(favoriteEntity)
    }

    override suspend fun deleteFavouriteCity(id: Int) {
        favoriteEntities.removeIf{
            it.id == id
        }
    }

    override fun getAlert(): Flow<List<AlertEntity>> = flow {
        emit(alertEntities)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity) {
        alertEntities.add(alertEntity)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        alertEntities.remove(alertEntity)
    }

    override fun getAlertById(id: String): AlertEntity {
        return alertEntities[id.toInt()]
    }
}