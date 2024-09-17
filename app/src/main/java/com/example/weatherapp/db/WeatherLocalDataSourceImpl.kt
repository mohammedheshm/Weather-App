package com.example.weatherapp.db

import android.content.Context
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context) : WeatherLocalDataSource {

    private val weatherDao: WeatherDao by lazy {
        val database = WeatherDatabase.getInstance(context)
        database.getWeatherDao()
    }

    override fun getWeatherResponse(): Flow<WeatherResponse> {
        return weatherDao.getWeatherResponse()
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        weatherDao.insertWeatherResponse(weatherResponse)
    }

    override suspend fun insertAlert(alert: Alert?) {
        weatherDao.insertAlert(alert!!)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return weatherDao.getAlerts()
    }

    override suspend fun deleteAlert(alert: Alert?) {
        weatherDao.deleteAlert(alert!!)

    }

    override suspend fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        weatherDao.deleteFavorite(favoriteWeather)
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {


        weatherDao.insertFavorite(favoriteWeather)


    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return weatherDao.getAllFavorites()
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        return weatherDao.getFavoriteById(favoriteId)
    }


}