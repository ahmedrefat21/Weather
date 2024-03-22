package com.example.weatherforecast.model

import WeatherResponse
import com.example.weatherforecast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImpl private constructor(
    private var weatherRemoteDataSource: RemoteDataSource
    ) : Repository {


    companion object{
        private var instance: RepositoryImpl? = null
        fun getInstance(
            weatherRemoteDataSource: RemoteDataSource
        ) : RepositoryImpl {
            return instance ?: synchronized(this){
                val temp = RepositoryImpl(
                    weatherRemoteDataSource)
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
        return flowOf(weatherRemoteDataSource.getWeatherOverNetwork(lat,lon,units,lang))
    }


}