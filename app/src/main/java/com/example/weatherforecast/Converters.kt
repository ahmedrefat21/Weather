package com.example.weatherforecast


import androidx.room.TypeConverter
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Daily
import com.example.weatherforecast.model.Hourly
import com.google.gson.Gson

class Converters {


    @TypeConverter
    fun currentToString(current: Current): String = Gson().toJson(current)

    @TypeConverter
    fun stringToCurrent(current: String): Current = Gson().fromJson(current,Current::class.java)

    @TypeConverter
    fun hourlyToString(hourly: List<Hourly>) :String= Gson().toJson(hourly)

    @TypeConverter
    fun stringToHourly(hourly: String) = Gson().fromJson(hourly, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun dailyToString(daily: List<Daily>) :String= Gson().toJson(daily)

    @TypeConverter
    fun stringToDaily(daily: String) = Gson().fromJson(daily, Array<Daily>::class.java).toList()

    @TypeConverter
    fun alertToString(alert: List<Alert>) :String= Gson().toJson(alert)

    @TypeConverter
    fun stringToAlert(alert: String) = Gson().fromJson(alert, Array<Alert>::class.java).toList()
}