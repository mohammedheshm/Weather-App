package com.example.weatherapp.alert.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.data.source.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule


@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {

    // Test rules
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Dispatcher for coroutines testing
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var viewModel: AlertViewModel




}