package com.example.weatherapp.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.source.FakeWeatherRepository
import com.example.weatherapp.map.viewmodel.MapViewModel
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var fakeWeatherRepository: FakeWeatherRepository
    lateinit var mapViewModel: MapViewModel


    @Before
    fun setUp() {
        fakeWeatherRepository = FakeWeatherRepository()
        favoriteViewModel = FavoriteViewModel(fakeWeatherRepository)
        mapViewModel = MapViewModel(fakeWeatherRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    // 1- Test function of setFavDetailsLocation
    @Test
    fun setFavoriteDetailsLocation_setsLocation_UpdatesFavorite() {

        // Given a valid latitude and longitude
        val latitude = "26"
        val longitude = "30"

        //When set favorite details location
        favoriteViewModel.setFavDetailsLocation(latitude, longitude)


        //Then checks that favWeatherDetails is not null
        val result = favoriteViewModel.favWeatherDetails
        assertThat(result, not(nullValue()))

    }

    // 2- Test Function of getFavoriteWeatherFromDataBase
    @Test
    fun getFavoriteWeatherFromDatabase_setFavWeatherLocation_returnsSameFavorite() {

        // When
        favoriteViewModel.getFavoriteWeatherFromDataBase()

        // Then
        val result = favoriteViewModel.favoriteWeather
        assertThat(result, not(nullValue()))
    }

    // 3- Test Function of getFavoriteWeatherDetailsFromRemote
    @Test
    fun getFavoriteWeatherDetailsFromRemote_setFavWeatherLocation_returnsFavoriteForLocation() {

        // Given a valid latitude and longitude
        val latitude = "26"
        val longitude = "30"

        //When get favorite weather details from remote
        favoriteViewModel.getFavoriteWeatherDetailsFromRemote(latitude, longitude)

        //Then
        assertThat(favoriteViewModel.favWeatherDetails.value, not(nullValue()))
        assertThat(favoriteViewModel.favWeatherDetails.value, `is`(ApiState.Loading))

    }


    // 4- Test Function of deleteFavorite
    @Test
    fun deleteFavorite_removesFavoriteFromRepository() {

        // Given valid Id ,latitude and longitude
        val favoriteWeather = FavoriteWeather(1, 26.0, 30.0)

        // When delete favorite by id
        favoriteViewModel.deleteFavorite(favoriteWeather)

        // Then Assuming the ViewModel Show a list of favorites ,Assert the item is no longer in the list
        val result = favoriteViewModel.favoriteWeather.value
        assertThat(result, not(hasItem(favoriteWeather)))
    }

    // 5- Test Function of getFavoriteById
    @Test
    fun getFavoriteById_FavoriteId_returnsSameFavorite() = runTest {

        // Given
        val favoriteWeather = FavoriteWeather(1, 26.0, 30.0)
        mapViewModel.insertFavoriteWeather(favoriteWeather)

        // When
        favoriteViewModel.getFavoriteById(favoriteWeather.fav_id)


        // Then
        val result = favoriteViewModel.favoriteWeatherById.value
        assertThat(result, `is`(favoriteWeather))


    }

}