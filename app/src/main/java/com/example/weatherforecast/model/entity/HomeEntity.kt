package com.example.weatherforecast.model.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Daily
import com.example.weatherforecast.model.Hourly

@Entity(tableName = "currentWeather")
data class HomeEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                      var lat:Double, var lon:Double, var current: Current?,
                      var hourly:List<Hourly>, var daily:List<Daily>) {

    constructor():this(1,0.0,0.0,null, emptyList(), emptyList())

}