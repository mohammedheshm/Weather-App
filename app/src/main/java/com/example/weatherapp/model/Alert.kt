package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_table")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var location: String = "",
    val date: String = "",
    val time: String = "",
    var event: String = "",
    var start: Long = 0,
    var end: Long = 0,
    var description: String = "",
)
