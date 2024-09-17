package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): WeatherResponse
}