package com.example.weatherforecast.model


import com.example.weatherforecast.local.LocalDataSource
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import com.example.weatherforecast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImpl private constructor(
    private var remoteDataSource: RemoteDataSource,
    private var localDataSource : LocalDataSource

) : Repository {


    companion object{
        private var instance: RepositoryImpl? = null
        fun getInstance(
            weatherRemoteDataSource: RemoteDataSource,
            localDataSource : LocalDataSource

        ) : RepositoryImpl {
            return instance ?: synchronized(this){
                val temp = RepositoryImpl(
                    weatherRemoteDataSource,
                    localDataSource)
                instance = temp
                temp
            }

        }
    }
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        return flowOf(remoteDataSource.getWeatherOverNetwork(lat,lon,units,lang))
    }

    override fun getCurrentWeather(): Flow<HomeEntity> {
        return localDataSource.getCurrentWeather()
    }

    override suspend fun insertCurrentWeather(homeEntity: HomeEntity) {
        return localDataSource.insertCurrentWeather(homeEntity)
    }

    override suspend fun deleteCurrentWeather() {
        return localDataSource.deleteCurrentWeather()
    }


    override fun getFavoriteCity(): Flow<List<FavoriteEntity>> {
        return localDataSource.getFavouriteCity()
    }

    override suspend fun insertFavoriteCity(favoriteEntity: FavoriteEntity) {
        return localDataSource.insertFavouriteCity(favoriteEntity)
    }

    override suspend fun deleteFavoriteCity(id : Int) {
        return localDataSource.deleteFavouriteCity(id)
    }

    override fun getAlert(): Flow<List<AlertEntity>> = localDataSource.getAlert()
    override suspend fun insertAlert(alertEntity: AlertEntity) {
        localDataSource.insertAlert(alertEntity)
    }
    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        localDataSource.deleteAlert(alertEntity)
    }
    override fun getAlertById(id: String): AlertEntity {
        return localDataSource.getAlertById(id)
    }

    override suspend fun getWeatherAlert(lat: Double,lon: Double,units: String,lang:String): WeatherResponse {
        return remoteDataSource.getWeatherOverNetwork(lat,lon,units,lang)
    }


}