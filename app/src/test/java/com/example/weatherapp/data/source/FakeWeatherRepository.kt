package com.example.weatherapp.data.source

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepo
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository :WeatherRepo {
    override fun getWeatherResponse(): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override fun getCurrentWeatherFromRemote(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert?) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: Alert?) {
        TODO("Not yet implemented")
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        TODO("Not yet implemented")
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        TODO("Not yet implemented")
    }
}