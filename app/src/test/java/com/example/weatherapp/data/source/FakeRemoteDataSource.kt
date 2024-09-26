package com.example.weatherapp.data.source

import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherRemoteDataSource

class FakeRemoteDataSource(private val weatherResponse: WeatherResponse) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): WeatherResponse {
        return weatherResponse
    }
}