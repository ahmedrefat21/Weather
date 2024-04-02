package com.example.weatherforecast.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.model.entity.HomeEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(homeEntity: HomeEntity)
    @Query("DELETE FROM currentWeather")
    suspend fun deleteCurrentWeather()
    @Query("SELECT * FROM currentWeather")
    fun getCurrentWeather(): Flow<HomeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteCity(favoriteEntity : FavoriteEntity)
    @Query("DELETE FROM favoritePlace WHERE id = :id")
    suspend fun deleteFavouriteCity(id : Int)
    @Query("SELECT * FROM favoritePlace")
    fun getAllFavouriteCities(): Flow<List<FavoriteEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alertEntity: AlertEntity)
//    @Query("DELETE FROM Alert")
    @Delete
    suspend fun deleteAlert(alertEntity: AlertEntity)
    @Query("SELECT * FROM Alert")
    fun getAllAlerts(): Flow<List<AlertEntity>>
    @Query("SELECT * FROM Alert Where id=:id")
    fun getAlertById(id:String) : AlertEntity




}