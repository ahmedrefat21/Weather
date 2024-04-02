package com.example.weatherforecast.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@ExperimentalCoroutinesApi
@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    private lateinit var dataBase: WeatherDataBase
    private lateinit var weatherDao: WeatherDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = dataBase.getWeather()

    }

    @After
    fun teardown() = dataBase.close()

    @Test
    fun insertFavourite() = runBlockingTest {
        //Given
        val favourite = FavoriteEntity()
        weatherDao.insertFavouriteCity(favourite)

        //When
        val result = weatherDao.getAllFavouriteCities().first()

        // Then
        assertTrue(result.contains(favourite))
    }
    @Test
    fun deleteFavourite() = runBlockingTest {
        // Given
        val favourite = FavoriteEntity()
        weatherDao.insertFavouriteCity(favourite)

        // When
        weatherDao.deleteFavouriteCity(favourite.id)
        val result = weatherDao.getAllFavouriteCities().first()

        // Then
        assertFalse(result.contains(favourite))
    }
    @Test
    fun insertAlert() = runBlockingTest {
        // Given
        val alertToInsert = createSampleAlertEntity("1")
        // When
        weatherDao.insertAlert(alertToInsert)
        // Then
        val result = weatherDao.getAllAlerts().first()
        assertTrue(result.contains(alertToInsert))
    }
    @Test
    fun deleteAlert() = runBlockingTest {

        // Given
        val alertToDelete = createSampleAlertEntity("1")
        weatherDao.insertAlert(alertToDelete)

        // When
        weatherDao.deleteAlert(alertToDelete)

        // Then
        val result = weatherDao.getAllAlerts().first()
        assertFalse(result.contains(alertToDelete))
    }
    @Test
    fun getAlertById() = runBlockingTest {
        //Given
        val alert1 = createSampleAlertEntity("1")
        val alert2 = createSampleAlertEntity("2")
        weatherDao.insertAlert(alert1)
        weatherDao.insertAlert(alert2)

        // When
        val result = weatherDao.getAlertById("2")
        // Then
        assertThat(result, equalTo(alert2))
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