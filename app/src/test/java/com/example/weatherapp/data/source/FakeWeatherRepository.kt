package com.example.weatherapp.data.source

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherRepository : WeatherRepo {

    // Mock data
    private val weatherResponses = mutableListOf<WeatherResponse>()
    private val alerts = mutableListOf<Alert>()
    private val favoriteWeathers = mutableListOf<FavoriteWeather>()

    override fun getWeatherResponse(): Flow<WeatherResponse> {
        // Returning the last weather response or a mock response
        return flowOf(weatherResponses.lastOrNull() ?: mockWeatherResponse())
    }

    override fun getCurrentWeatherFromRemote(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): Flow<WeatherResponse> {
        // Mock remote weather data for testing
        val mockWeather = mockWeatherResponse(lat, lon)
        weatherResponses.add(mockWeather)
        return flowOf(mockWeather)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        // Returning the list of mock alerts
        return flowOf(alerts)
    }

    override suspend fun insertAlert(alert: Alert?) {
        alert?.let { alerts.add(it) }
    }

    override suspend fun deleteAlert(alert: Alert?) {
        alerts.remove(alert)
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        // Returning the list of favorite mock weather locations
        return flowOf(favoriteWeathers)
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        // Returning a mock favorite weather based on the ID
        return flowOf(favoriteWeathers.firstOrNull { it.fav_id == favoriteId }
            ?: mockFavoriteWeather(favoriteId))
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        favoriteWeathers.add(favoriteWeather)
    }

    override suspend fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        favoriteWeathers.remove(favoriteWeather)
    }

    // Mock data generation methods
    private fun mockWeatherResponse(lat: String = "0.0", lon: String = "0.0"): WeatherResponse {
        // Return a mock WeatherResponse object
        return WeatherResponse(
            lat = lat.toDouble(),
            lon = lon.toDouble(),
            timezone = "UTC",
        )
    }

    private fun mockFavoriteWeather(id: Int): FavoriteWeather {
        return FavoriteWeather(
            fav_id = id,
        )
    }
}
