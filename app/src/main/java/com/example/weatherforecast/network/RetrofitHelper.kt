package com.example.weatherforecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    private const val baseURL = "https://api.openweathermap.org/data/3.0/"
    val retrofitInstance= Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}