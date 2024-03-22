package com.example.weatherforecast.network

import WeatherResponse

class RemoteDataSourceImpl private constructor() : RemoteDataSource {

    private val retrofitService : WeatherService by lazy {
        RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
    }
    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        val response = retrofitService.getWeather(lat ,lon ,units=units, lang = lang)
        return response
    }

    companion object{
        private var instance: RemoteDataSourceImpl? = null
        fun getInstance() : RemoteDataSourceImpl {
            return instance ?: synchronized(this){
                val temp = RemoteDataSourceImpl()
                instance = temp
                temp
            }

        }
    }
}