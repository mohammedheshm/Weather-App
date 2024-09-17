package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

sealed class ApiState {
    class Success(val data: WeatherResponse) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    data object Loading : ApiState()
}