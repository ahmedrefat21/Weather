package com.example.weatherforecast.network


import com.example.weatherforecast.Constants
import com.example.weatherforecast.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat :Double,
        @Query("lon") lon:Double,
        @Query("appid")appID:String = Constants.API_KEY,
        @Query("exclude") exclude:String = Constants.EXCLUDE,
        @Query("units") units:String,
        @Query("lang") lang:String): WeatherResponse

}