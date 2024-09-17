package com.example.weatherapp.db

import android.util.Log
import androidx.room.TypeConverter
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.model.HourlyForecast
import com.example.weatherapp.model.WeatherDetails
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object Converters {

    @TypeConverter
    fun fromCurrentWeather(currentWeather: CurrentWeather): String {
        return Gson().toJson(currentWeather)
    }

    @TypeConverter
    fun toCurrentWeather(json: String): CurrentWeather {
        val objectType = object : TypeToken<CurrentWeather>() {}.type
        return Gson().fromJson(json, objectType)
    }

    @TypeConverter
    fun toWeatherDetails(json: String): WeatherDetails {
        val listType = object : TypeToken<List<WeatherDetails>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun fromWeatherDetails(weatherDetails: WeatherDetails): String {
        return Gson().toJson(weatherDetails)
    }


    @TypeConverter
    fun toDailyForecast(value: String): List<DailyForecast> {
        val listType = object : TypeToken<List<DailyForecast>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDailyForecast(list: List<DailyForecast>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toHourlyForecast(value: String): List<HourlyForecast> {
        val listType = object : TypeToken<List<HourlyForecast>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromHourlyForecast(list: List<HourlyForecast>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toAlert(value: String): List<Alert>? {
        return try {
            val listType = object : TypeToken<List<Alert>>() {}.type
            Gson().fromJson(value, listType)
        } catch (e: JsonSyntaxException) {
            Log.e("Converters", "Error parsing JSON to Alert list: $e")
            null
        }
    }

    @TypeConverter
    fun fromAlert(list: List<Alert>?): String {
        return if (list != null) {
            Gson().toJson(list)
        } else {
            "The Alert is null"
        }
    }

    fun fromTag(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

}