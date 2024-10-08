package com.example.weatherapp.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repository.WeatherRepo

class AlertViewModelFactory(private val _repo: WeatherRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(_repo) as T
        } else {

            throw IllegalArgumentException("AlertViewModel Class Not Found")
        }
    }


}