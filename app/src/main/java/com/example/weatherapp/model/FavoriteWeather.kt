package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteWeather(
    @PrimaryKey(autoGenerate = true)
    var fav_id: Int = 0,
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var timezone: String = "",
)
