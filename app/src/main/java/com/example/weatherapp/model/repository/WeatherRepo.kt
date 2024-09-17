package com.example.weatherapp.model.repository

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

    fun getWeatherResponse(): Flow<WeatherResponse>

    fun getCurrentWeatherFromRemote(
        lat: String = "",
        lon: String = "",
        language: String = "",
        units: String = ""
    ): Flow<WeatherResponse>


    fun getAllAlerts(): Flow<List<Alert>>
    fun getAllFavorites(): Flow<List<FavoriteWeather>>
    suspend fun insertAlert(alert: Alert?)
    suspend fun insertFavorite(favoriteWeather: FavoriteWeather)
    suspend fun deleteAlert(alert: Alert?)
    suspend fun deleteFavorite(favoriteWeather: FavoriteWeather)
    fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather>
}