package com.example.weatherapp.alert.view

import com.example.weatherapp.model.Alert

interface OnAlertItemClick {
    fun deleteAlert(alert: Alert)
}