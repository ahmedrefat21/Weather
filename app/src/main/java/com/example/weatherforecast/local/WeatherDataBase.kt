package com.example.weatherforecast.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.Converters
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity

@Database(entities = [HomeEntity::class, AlertEntity::class, FavoriteEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeather() : WeatherDao
    companion object{
        private var INSTANCE : WeatherDataBase? = null

        fun getInstance (context: Context) : WeatherDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, WeatherDataBase::class.java,"WeatherDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}