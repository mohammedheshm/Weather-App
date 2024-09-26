package com.example.weatherapp.data.source

import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepo
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository : WeatherRepo {

    private var localDataSource: FakeLocalDataSource = FakeLocalDataSource()
    private lateinit var remoteDataSource: FakeRemoteDataSource

    override fun getWeatherResponse(): Flow<WeatherResponse> {

        return localDataSource.getWeatherResponse()

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
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert?) {

        return localDataSource.insertAlert(alert)

    }

    override suspend fun deleteAlert(alert: Alert?) {

        return localDataSource.deleteAlert(alert)
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return localDataSource.getAllFavorites()
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        return localDataSource.getFavoriteById(favoriteId)
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        return localDataSource.insertFavorite(favoriteWeather)
    }

    override suspend fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        return localDataSource.deleteFavorite(favoriteWeather)
    }

}