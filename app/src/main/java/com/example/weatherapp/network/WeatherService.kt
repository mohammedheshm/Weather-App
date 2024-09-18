package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {

    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appId: String = "e458d81824ca8cd85c01327409e255e5"
    ): WeatherResponse
}
