package com.example.weatherapp.data.source

import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource : WeatherLocalDataSource {

    private val weatherResponses = mutableListOf<WeatherResponse>()
    private val alerts = mutableListOf<Alert>()
    private val favoriteWeathers = mutableListOf<FavoriteWeather>()


    // Return the first WeatherResponse or emit an error/empty response if none exist
    override fun getWeatherResponse(): Flow<WeatherResponse> {
        return flow {
            val weatherResponse = weatherResponses.firstOrNull()
                ?: WeatherResponse() // Emit an empty WeatherResponse if none exist
            emit(weatherResponse)
        }
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        weatherResponses.add(weatherResponse)
    }

    override suspend fun insertAlert(alert: Alert?) {
        alert?.let { alerts.add(it) }
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        favoriteWeathers.add(favoriteWeather)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return flowOf(alerts) // Return the current list of alerts as a Flow
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return flowOf(favoriteWeathers) // Return the current list of favorites as a Flow
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        // Return the favorite weather that matches the provided ID
        return flow {
            emit(favoriteWeathers.first { it.fav_id == favoriteId }) // Emit the found favorite
        }
    }

    override suspend fun deleteAlert(alert: Alert?) {
        alert?.let { alerts.remove(it) }
    }

    override suspend fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        favoriteWeathers.remove(favoriteWeather)
    }
}