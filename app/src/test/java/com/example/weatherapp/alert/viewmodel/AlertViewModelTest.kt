package com.example.weatherapp.alert.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.source.FakeWeatherRepository
import com.example.weatherapp.model.Alert
import com.example.weatherapp.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertViewModelTest {

    lateinit var alertViewModel: AlertViewModel
    lateinit var fakeWeatherRepository: FakeWeatherRepository

    @Before
    fun setUp() {
        fakeWeatherRepository = FakeWeatherRepository()
        alertViewModel = AlertViewModel(fakeWeatherRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)

    }


    // 1- Test Function of getAllAlerts
    @Test
    fun getAllAlerts_multipleAlerts_allAlertsRetrieved() = runTest {

        // Given multiple alerts
        val alert1 = Alert(id = 1, description = "Alert 1")
        val alert2 = Alert(id = 2, description = "Alert 2")
        alertViewModel.insertAlert(alert1)
        alertViewModel.insertAlert(alert2)

        // When getting all alerts
        alertViewModel.getAllAlerts()

        // Then verify all alerts are retrieved
        val result = alertViewModel.allAlerts.value
        assertThat(result.size, `is`(2)) // Expecting 2 alerts
        assertThat(
            result,
            contains(alert1, alert2)
        ) // Verifying both alerts are present and in correct order

    }


    // 2- Test Function of setAllAlertsLocation
    @Test
    fun setAlertLocation_validLocation_alertWeatherIsLoading() {

        // Given a valid latitude and longitude
        val latitude = "26"
        val longitude = "30"

        // When setting the alert location
        alertViewModel.setAlertLocation(latitude, longitude)

        // Then verify that the alertWeather state is initially Loading
        assertThat(alertViewModel.alertWeather, not(nullValue()))
        assertThat(alertViewModel.alertWeather.value, `is`(ApiState.Loading))

    }


    // 3- Test Function of getAlertWeather
    @Test
    fun getAlertWeather_setLocation_alertOfThisLocation() {

        //Given
        val latitude = "26"
        val longitude = "30"

        //When
        alertViewModel.getAlertWeather(latitude, longitude)

        //Then
        assertThat(alertViewModel.alertWeather, not(nullValue()))
        assertThat(alertViewModel.alertWeather.value, `is`(ApiState.Loading))

    }

    // 4- Test Function of deleteAlert From database
    @Test
    fun deleteAlert_existingAlert_alertRemoved() = runTest {

        // Given an alert to insert and then delete
        val alert = Alert(id = 1, description = "Test Alert")
        alertViewModel.insertAlert(alert) // First insert the alert

        // When deleting the alert
        alertViewModel.deleteAlert(alert)

        // Then verify that the alert is no longer present in the repository
        val allAlerts = alertViewModel.allAlerts.value
        assertThat(allAlerts.size, `is`(0)) // Expecting 0 alerts after deletion
    }

}