package com.example.weatherapp.db

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

    fun getWeatherResponse(): Flow<WeatherResponse>
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)
    suspend fun insertAlert(alert: Alert?)
    suspend fun insertFavorite(favoriteWeather: FavoriteWeather)
    fun getAllAlerts(): Flow<List<Alert>>
    fun getAllFavorites(): Flow<List<FavoriteWeather>>
    fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather>
    suspend fun deleteAlert(alert: Alert?)
    suspend fun deleteFavorite(favoriteWeather: FavoriteWeather)

}