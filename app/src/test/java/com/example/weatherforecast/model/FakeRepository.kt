package com.example.weatherforecast.model

import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : Repository {

    private var homeEntity : MutableList<HomeEntity> = mutableListOf()
    private var favoriteEntity : MutableList<FavoriteEntity> = mutableListOf()
    private var alertEntity : MutableList<AlertEntity> = mutableListOf()
    private lateinit var current:Current
    private lateinit var alert : List<Alert>
    private lateinit var hourly:List<Hourly>
    private lateinit var daily:List<Daily>


    init {
        initializeData()
    }


    private fun initializeData() {
        val weather = listOf(Weather("", "", 0, ""))
        alert = listOf(Alert("", 0, "", "", 0, listOf()))
        current = Current(
            1, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0, weather, 0, 0.0
        )
         hourly = listOf(Hourly(0, 0.0, 0, 0.0, 0, 0.0, 0, 0.0, 0.0, 0, weather, 0, 0.0, 0.0))
         daily = listOf(
            Daily(
                0, 0.0, 0, FeelsLike(0.0, 0.0, 0.0, 0.0),
                0, 0.0, 0, 0, 0.0, 0, 0.0, "", 0, 0, Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), 0.0,
                weather, 0, 0.0, 0.0
            )
        )


    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        return  flowOf(WeatherResponse(
            0,alert,current,daily,hourly,0.0,0.0,"",0))
    }

    override fun getCurrentWeather(): Flow<HomeEntity> = flowOf()

    override suspend fun insertCurrentWeather(homeEntity: HomeEntity) {
        this.homeEntity.add(homeEntity)
    }

    override suspend fun deleteCurrentWeather() {
        homeEntity.clear()
    }

    override fun getFavoriteCity(): Flow<List<FavoriteEntity>> = flow {
        emit(favoriteEntity)
    }

    override suspend fun insertFavoriteCity(favoriteEntity: FavoriteEntity) {
        this.favoriteEntity.add(favoriteEntity)
    }

    override suspend fun deleteFavoriteCity(id: Int) {
        favoriteEntity.removeIf{
            it.id == id
        }
    }

    override fun getAlert(): Flow<List<AlertEntity>> = flow {
        emit(alertEntity)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity) {
        this.alertEntity.add(alertEntity)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        this.alertEntity.remove(alertEntity)
    }

    override fun getAlertById(id: String): AlertEntity {
        return alertEntity[id.toInt()]
    }

    override suspend fun getWeatherAlert(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
       return WeatherResponse(
           0,alert,current,daily,hourly,0.0,0.0,"",0)
    }
}