package com.example.weatherapp.data.source

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepoImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class DefaultWeatherRepositoryTest {

    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var weatherRepository: WeatherRepoImpl

    private val weather1 = WeatherResponse(1)
    private val weather2 = WeatherResponse(2)
    private val weather3 = WeatherResponse(3)
    private val weather4 = WeatherResponse(4)
    private val weather5 = WeatherResponse(5)
    private val weather6 = WeatherResponse(6)

    private val alert1 = Alert(1)
    private val fav1 = FavoriteWeather(1)

    val localWeather = mutableListOf<WeatherResponse>(
        weather1, weather2, weather3
    )

    val remoteWeather = mutableListOf<WeatherResponse>(
        weather4, weather5, weather6
    )


    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource(weather1)
        weatherRepository = WeatherRepoImpl.getInstance(fakeRemoteDataSource, fakeLocalDataSource)

    }


    // 1- Test Function of getCurrentWeatherFromRemote
    @Test
    fun getCurrentWeatherFromRemote_remoteWeathers_returnsSame() = runTest {

        // When calling getCurrentWeatherFromRemote
        val result = weatherRepository.getCurrentWeatherFromRemote()

        //Then result.data is remote
        val result2 = mutableListOf<WeatherResponse>()
        result.collect { weatherResponse ->
            result2.add(weatherResponse)
        }

        assertThat(result2.size, `is`(1))
        assertThat(result2[0], `is`(weather1))


    }

    // 2- Test Function of getAllAlerts from local database
    @Test
    fun getAllAlerts_setAlert_returnsAllAlerts() = runTest {
        // Given
        fakeLocalDataSource.insertAlert(alert1)

        // When
        val result = weatherRepository.getAllAlerts()

        // Then
        val alerts = result.toList() // Collect flow results
        assertThat(alerts.size, `is`(1))
        assertThat(alerts[0][0], `is`(alert1))
    }

    // 3- Test Function of insertAlert from local database
    @Test
    fun insertAlert_setAlert_insertsAlertCorrectly() = runTest {
        // When
        weatherRepository.insertAlert(alert1)

        // Then
        val alerts = fakeLocalDataSource.getAllAlerts().toList() // Collect flow results
        assertThat(alerts.size, `is`(1))
        assertThat(alerts[0][0], `is`(alert1))
    }


    // 4- Test Function of getAllFavorites from local database
    @Test
    fun getAllFavorites_setFav_returnsAllFavorites() = runTest {
        // Given
        fakeLocalDataSource.insertFavorite(fav1)

        // When
        val result = weatherRepository.getAllFavorites()

        // Then
        val favorites = result.toList() // Collect flow results
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0][0], `is`(fav1))
    }

    // 5- Test Function of insertFavorite from local database
    @Test
    fun insertFavorite_setFav_insertsFavoriteCorrectly() = runTest {
        // When
        weatherRepository.insertFavorite(fav1)

        // Then
        val favorites = fakeLocalDataSource.getAllFavorites().toList() // Collect flow results
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0][0], `is`(fav1))
    }


    // 6- Test Function of getFavoriteById from local database
    @Test
    fun getFavoriteById_returnsCorrectFavorite() = runTest {
        // Given
        fakeLocalDataSource.insertFavorite(fav1)

        // When
        val result = weatherRepository.getFavoriteById(fav1.fav_id)

        // Then
        val favorites = result.toList() // Collect flow results
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0], `is`(fav1))
    }


}