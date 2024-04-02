package com.example.weatherforecast.favourite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.map.viewmodel.MapViewModel
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.FakeRepository
import com.example.weatherforecast.model.FavouriteState
import com.example.weatherforecast.model.entity.FavoriteEntity
import org.junit.Assert.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest{

    private lateinit var repo : FakeRepository
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var mapViewModel: MapViewModel


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        repo = FakeRepository()
        viewModel = FavouriteViewModel(repo)
        mapViewModel = MapViewModel(repo)
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
    fun getFavoritePlace_returnListNotNull()= runBlocking {

        //When
        viewModel.getFavoriteWeather()

        //Then
        val result = viewModel.favorite.first()
        var data = emptyList<FavoriteEntity>()
        when(result){
            is FavouriteState.Success ->
                data = result.data
            else ->{}

        }
        MatcherAssert.assertThat(data, CoreMatchers.`is`(CoreMatchers.notNullValue()))
    }


    @Test
    fun `delete favourite should remove specified favourite`() = runBlockingTest{

        //Given
        val favouriteToDelete = FavoriteEntity(id = 1,50.0,30.0,"london")
        val remainingFavourite = FavoriteEntity(id = 2,50.0,30.0,"Alex")

        //When
        mapViewModel.insertFavoriteCity(favouriteToDelete)
        mapViewModel.insertFavoriteCity(remainingFavourite)
        viewModel.deleteFavoriteWeather(favouriteToDelete.id)
        viewModel.getFavoriteWeather()

        //Then
        val job = launch {
            viewModel.favorite.collect {
                when (it) {
                    is FavouriteState.Success -> {
                        if (it.data.isNotEmpty()) {
                            // Verify that the deleted favourite is not present
                            Assert.assertFalse(it.data.contains(favouriteToDelete))
                            // Verify that the remaining favourite is still present
                            Assert.assertTrue(it.data.contains(remainingFavourite))


                        }
                    }
                    else -> {

                    }
                }
            }
        }
        job.cancel()

    }


    @Test
    fun `delete favorite place`() = runBlocking {
        // Given
        val favoritePlace = FavoriteEntity(id = 2, 50.0, 30.0, "London")
        mapViewModel.insertFavoriteCity(favoritePlace)

        // When
        val result = viewModel.deleteFavoriteWeather(favoritePlace.id)

        // Then
        assertEquals(Unit, result)
    }


//    @Test
//    fun `get favourite should return favourite items in the list`() = runBlockingTest {
//        //Given
//        val newFavourite = FavoriteEntity(id = 2,50.0,30.0,"london")
//
//        //When
//        mapViewModel.insertFavoriteCity(newFavourite)
//        viewModel.getFavoriteWeather()
//
//        //Then
//        launch {
//            viewModel.favorite.collect {
//                when (it) {
//                    is FavouriteState.Success -> {
//                        Assert.assertTrue(it.data.contains(newFavourite))
//                        cancel()
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }




}