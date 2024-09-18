package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repository.WeatherRepo

class MapViewModelFactory(private val _repo: WeatherRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(_repo) as T
        } else {

            throw IllegalArgumentException("Map View Model Class Not Found")
        }
    }


}