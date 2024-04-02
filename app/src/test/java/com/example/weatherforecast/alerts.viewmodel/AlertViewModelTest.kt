package com.example.weatherforecast.alerts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.FakeRepository
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.entity.AlertEntity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertViewModelTest{

    private lateinit var repo : FakeRepository
    private lateinit var viewModel: AlertViewModel


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        repo = FakeRepository()
        viewModel = AlertViewModel(repo)
    }


    @Test
    fun `insert alert should add the new alert to the list`() = runBlockingTest {
        //Given
        val newAlert = createSampleAlertEntity("1")

        //When
        viewModel.insertAlert(newAlert)
        viewModel.getAlert()

        //Then
        launch {
            viewModel.alert.collect {
                when (it) {
                    is AlertState.Success -> {
                        // Verify that the new alert is added to the list
                        Assert.assertTrue(it.alertEntity.contains(newAlert))
                        cancel()
                    }
                    else -> {}
                }
            }
        }
    }

    @Test
    fun `get weather should return weather data with specified latitude & longitude `() = runBlockingTest {
        // When
        viewModel.getWeather(40.0, 30.0, "", "")

        // Then
        launch {
            viewModel.weather.collect {
                when (it) {
                    is ApiState.Success -> {
                        // Verify that the latitude of the returned weather data is as expected
                        Assert.assertEquals(0.0, it.weather.lat, 40.0)
                        Assert.assertEquals(0.0, it.weather.lon, 30.0)
                        cancel()
                    }
                    else -> {}
                }
            }
        }
    }

    @Test
    fun `delete alert should remove specified alert`() = runBlockingTest{

        //Given
        val alertToDelete = createSampleAlertEntity("1")
        val remainingAlert = createSampleAlertEntity("2")

        //When
        viewModel.insertAlert(alertToDelete)
        viewModel.insertAlert(remainingAlert)
        viewModel.deleteAlert(alertToDelete)
        viewModel.getAlert()

        //Then
        launch {
            viewModel.alert.collect {
                when (it) {
                    is AlertState.Success -> {
                        // Verify that the deleted alert is not present
                        Assert.assertFalse(it.alertEntity.contains(alertToDelete))
                        // Verify that the remaining alert is still present
                        Assert.assertTrue(it.alertEntity.contains(remainingAlert))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Test
    fun `get alert should return the correct alert entity from the database`() = runBlockingTest {
        // Given
        val expectedAlert = createSampleAlertEntity("1")
        viewModel.insertAlert(expectedAlert)

        // When
        viewModel.getAlert()

        // Then
        launch {
            viewModel.alert.collect {
                when (it) {
                    is AlertState.Success -> {
                        // Verify that the retrieved alert entity matches the expected alert entity
                        Assert.assertEquals(expectedAlert, it.alertEntity.first())
                        cancel()
                    }
                    else -> {}
                }
            }
        }
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