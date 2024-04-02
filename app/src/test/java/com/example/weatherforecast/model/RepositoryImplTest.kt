package com.example.weatherforecast.model

import com.example.weatherforecast.local.FakeLocalDataSource
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.entity.HomeEntity
import com.example.weatherforecast.network.FakeRemoteDataSource
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RepositoryImplTest{

    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var repo: RepositoryImpl

    @Before
    fun getSetup(){
        val testData = createTestData("")
        fakeRemoteDataSource = FakeRemoteDataSource(testData.weatherResponse)
        fakeLocalDataSource = FakeLocalDataSource(testData.homeEntities, testData.favorites, testData.alertEntities)
        repo = RepositoryImpl.getInstance(fakeRemoteDataSource, fakeLocalDataSource)
    }


    @Test
    fun `getAlertById should return the correct alert entity`() = runBlockingTest {
        // When
        val alert = repo.getAlertById((createTestData("").alertEntities[0].id))

        // Then
        assertThat(alert, equalTo((createTestData("").alertEntities[0])))
    }

    @Test
    fun `getWeatherAlert should return the correct weather response`() = runBlockingTest {
        // When
        val response = repo.getWeatherAlert(0.0, 0.0, "", "")

        // Then
        assertThat(response, equalTo(createTestData("").weatherResponse))
    }


    @Test
    fun `insert favorite should increment last favorite's ID`() = runBlockingTest {
        // Given
        val favorite = FavoriteEntity(5, 0.0, 0.0, "")

        // When
        repo.insertFavoriteCity(favorite)

        // Then
        var lastFavoriteId = 0
        repo.getFavoriteCity().collect { favorites ->
            lastFavoriteId = favorites.last().id
        }
        assertThat(lastFavoriteId, equalTo(5))

    }


    @Test
    fun `insert favorite should contain the added favorite`() = runBlockingTest {
        // Given
        val favorite = FavoriteEntity(5, 0.0, 0.0, "")

        // When
        repo.insertFavoriteCity(favorite)

        // Then
        var favoritesList = listOf<FavoriteEntity>()
        repo.getFavoriteCity().collect { favorites ->
            favoritesList = favorites
        }
        assertThat(favoritesList.contains(favorite), equalTo(true))
    }

    @Test
    fun `insertAlert should add the alert entity to the database`() = runBlockingTest {
        // Given
        val alert = createTestData("5").alertEntity


        // When
        repo.insertAlert(alert)

        // Then
        var alertsList = listOf<AlertEntity>()
        repo.getAlert().collect { alerts ->
            alertsList = alerts
        }

        // Assert
        assertThat(alertsList.contains(alert), equalTo(true))
    }

    @Test
    fun `delete alert should remove the specified alert entity from the database`() = runBlockingTest {
        // Given
        val alert = createTestData("6").alertEntity

        repo.insertAlert(alert)

        // When
        repo.deleteAlert(alert)

        // Then
        var alertsList = listOf<AlertEntity>()
        repo.getAlert().collect { alerts ->
            alertsList = alerts
        }

        // Assert
        assertThat(alertsList.contains(alert), equalTo(false))
    }


    @Test
    fun `deleteFavoriteCity should remove the specified favorite from the database`() = runBlockingTest {
        // Given
        val favorite = FavoriteEntity(6, 0.0, 0.0, "")
        repo.insertFavoriteCity(favorite)

        // When
        repo.deleteFavoriteCity(favorite.id)

        // Then
        var favoritesList = listOf<FavoriteEntity>()
        repo.getFavoriteCity().collect { favorites ->
            favoritesList = favorites
        }
        // Assert
        assertThat(favoritesList.contains(favorite), equalTo(false))
    }



    private fun createTestData(id:String): TestData {
        val weather = listOf(Weather("", "", 0,""))
        val alert = listOf(Alert("",0,",","",0, listOf()))
        val current = Current(1, 0.0, 0, 0.0, 0, 0, 0,0,0.0,0.0,0, weather, 0,0.0)
        val hourly = listOf(Hourly(0,0.0,0,0.0,0,0.0,0,0.0,0.0,0,weather,0,0.0,0.0))
        val daily = listOf(Daily(0,0.0,0,FeelsLike(0.0,0.0,0.0,0.0),0,0.0,0,0,0.0,0,0.0,"",0,0,Temp(0.0,0.0,0.0,0.0,0.0,0.0),0.0,weather,0,0.0,0.0))

        val home = HomeEntity(1,30.0,30.0, current, hourly, daily)
        val favoriteCity1 = FavoriteEntity(1,30.0444,31.2357,"Cairo")
        val favoriteCity2 = FavoriteEntity(2,51.5074,-0.1278,"London")
        val favoriteCity3 = FavoriteEntity(4,40.7128,-74.0060,"New_York")
        val favoriteCity4 = FavoriteEntity(5,48.8566,2.3522,"Paris")

        val alert1 = AlertEntity("0",0.0,0.0,current, alert ,"","","")
        val alert2 = AlertEntity("1",0.0,0.0,current,alert,"","","")
        val alert3 = AlertEntity("3",0.0,0.0,current,alert,"","","")

        val favorites = listOf(favoriteCity1, favoriteCity2, favoriteCity3, favoriteCity4).toMutableList()
        val weatherResponse = WeatherResponse(0, alert, current, daily, hourly, 0.0, 0.0, "", 0)
        val homeEntities = listOf(home).toMutableList()
        val alertEntities = listOf(alert1, alert2, alert3).toMutableList()

        val alertEntity = AlertEntity(id,0.0,0.0,current, alert ,"","","")
        return TestData(weatherResponse, homeEntities, favorites, alertEntities, alertEntity)
    }

    data class TestData(
        val weatherResponse: WeatherResponse,
        val homeEntities: MutableList<HomeEntity>,
        val favorites: MutableList<FavoriteEntity>,
        val alertEntities: MutableList<AlertEntity>,
        val alertEntity: AlertEntity
    )
}

