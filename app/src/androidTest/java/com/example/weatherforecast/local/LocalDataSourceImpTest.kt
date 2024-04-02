package com.example.weatherforecast.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class LocalDataSourceImpTest{
    private lateinit var dataBase: WeatherDataBase
    private lateinit var localDataSource : LocalDataSourceImp

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        val testingContext : Application = ApplicationProvider.getApplicationContext()
        localDataSource = LocalDataSourceImp(testingContext)

    }

    @After
    fun teardown() = dataBase.close()

    @Test
    fun insertFavourite() = runBlockingTest {
        //Given
        val favourite = FavoriteEntity()
        localDataSource.insertFavouriteCity(favourite)

        //When
        val result = localDataSource.getFavouriteCity().first()

        // Then
        TestCase.assertTrue(result.contains(favourite))
    }
    @Test
    fun deleteFavourite() = runBlockingTest {
        // Given
        val favourite = FavoriteEntity()
        localDataSource.insertFavouriteCity(favourite)

        // When
        localDataSource.deleteFavouriteCity(favourite.id)
        val result = localDataSource.getFavouriteCity().first()

        // Then
        TestCase.assertFalse(result.contains(favourite))
    }
    @Test
    fun insertAlert() = runBlockingTest {
        // Given
        val alertToInsert = createSampleAlertEntity("1")
        // When
        localDataSource.insertAlert(alertToInsert)
        // Then
        val result = localDataSource.getAlert().first()
        TestCase.assertTrue(result.contains(alertToInsert))
    }
    @Test
    fun deleteAlert() = runBlockingTest {

        // Given
        val alertToDelete = createSampleAlertEntity("1")
        localDataSource.insertAlert(alertToDelete)

        // When
        localDataSource.deleteAlert(alertToDelete)

        // Then
        val result = localDataSource.getAlert().first()
        TestCase.assertFalse(result.contains(alertToDelete))
    }
    @Test
    fun getAlertById() = runBlockingTest {
        //Given
        val alert1 = createSampleAlertEntity("1")
        val alert2 = createSampleAlertEntity("2")
        localDataSource.insertAlert(alert1)
        localDataSource.insertAlert(alert2)

        // When
        val result = localDataSource.getAlertById("2")
        // Then
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(alert2))
    }

    private fun createSampleAlertEntity(id:String): AlertEntity {
        val weather = listOf(
            Weather(
                "overcast clouds",
                "04d",
                804,
                "Clouds"
            )
        )

        val alert = listOf(
            Alert(
                "SMALL CRAFT ADVISORY REMAINS IN EFFECT FROM 5 PM",
                1684952747,
                "Small Craft Advisory",
                "NWS Philadelphia",
                1684952747, listOf()
            )
        )

        val current = Current(
            100,
            290.68,
            1711909611,
            295.55,
            75,
            1012,
            1711886656,
            1711931742,
            295.32,
            5.01,
            1000,
            weather,
            170,
            4.17
        )

        return AlertEntity(id, 30.44, 35.6, current, alert, "notification", "20/3/2024", "8:43")

    }
}