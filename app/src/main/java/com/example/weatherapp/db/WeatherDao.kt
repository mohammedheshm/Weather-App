package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.Alert
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {


    @Query("SELECT * from weather_table")
    fun getWeatherResponse(): Flow<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    @Query("SELECT * from alert_table")
    fun getAlerts(): Flow<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert: Alert)

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM favorite_table WHERE fav_id = :favoriteId")
    fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather>

    @Query("SELECT * from favorite_table")
    fun getAllFavorites(): Flow<List<FavoriteWeather>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoriteWeather: FavoriteWeather)

    @Delete
    suspend fun deleteFavorite(favoriteWeather: FavoriteWeather)


}