package com.example.weatherforecast.map.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.FakeRepository
import com.example.weatherforecast.model.FavouriteState
import com.example.weatherforecast.model.entity.FavoriteEntity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapViewModelTest{


    lateinit var repo : FakeRepository
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var mapViewModel: MapViewModel


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        repo = FakeRepository()
        favouriteViewModel = FavouriteViewModel(repo)
        mapViewModel = MapViewModel(repo)
    }

    @Test
    fun `get weather should return weather data with specified latitude & longitude `() = runBlockingTest {
        // When
        mapViewModel.getWeather(40.0, 30.0, "", "")

        // Then
        launch {
            mapViewModel.weather.collect {
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
    fun `insert favourite should add the new favourite to the list`() = runBlockingTest {
        //Given
        val newAlert = FavoriteEntity(id = 2,40.0,30.0,"london")

        //When
        mapViewModel.insertFavoriteCity(newAlert)
        favouriteViewModel.getFavoriteWeather()

        //Then
        launch {
            favouriteViewModel.favorite.collect {
                when (it) {
                    is FavouriteState.Success -> {
                        // Verify that the new favourite is added to the list
                        Assert.assertTrue(it.data.contains(newAlert))
                        cancel()
                    }
                    else -> {}
                }
            }
        }
    }

}