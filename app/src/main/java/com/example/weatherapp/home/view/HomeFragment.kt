package com.example.weatherapp.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.WeatherLocalDataSourceImpl
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepoImpl
import com.example.weatherapp.network.ApiState
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.example.weatherapp.settings.view.LANG_KEY
import com.example.weatherapp.settings.view.TEMP_UNIT_KEY
import com.example.weatherapp.util.GPS
import com.example.weatherapp.util.INITIAL_CHOICE
import com.example.weatherapp.util.INITIAL_PREFS
import com.example.weatherapp.util.PermissionChecksUtil
import com.example.weatherapp.util.getAddress
import com.example.weatherapp.util.round
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val LOCATION = "Location"
const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val KELVIN = "kelvin"
const val CELSIUS = "celsius"
const val FAHRENHEIT = "fahrenheit"
const val METRIC = "metric"
const val STANDARD = "standard"
const val IMPERIAL = "imperial"

class HomeFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var homeDailyAdapter: HomeDailyAdapter
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private lateinit var initialSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""
    private var locationInitialPrefs = ""
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var binding: FragmentHomeBinding
    private val TAG = "HomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ")

        binding.cvDetails.visibility = View.GONE
        binding.cvCurrentWeather.visibility = View.GONE



        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.rvHourly.adapter = homeHourlyAdapter
        binding.rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        binding.rvDaily.adapter = homeDailyAdapter
        binding.rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)


        locationSharedPreferences =
            requireContext().getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

        prefsSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())


        initialSharedPreferences =
            requireContext().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)

        locationInitialPrefs =
            initialSharedPreferences.getString(INITIAL_CHOICE, "").toString()

        setupDataObserver()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.currentLocationSetting.collectLatest {
                    if (it == GPS)
                        getFreshLocation()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = locationResult.lastLocation

                    val longitude = location?.longitude.toString()
                    val latitude = location?.latitude.toString()

                    locationSharedPreferences.edit().putString(LATITUDE, latitude).apply()
                    locationSharedPreferences.edit().putString(LONGITUDE, longitude).apply()

                    fetchWeatherData()

                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()


        )
    }

    private fun setupDataObserver() {
        if (PermissionChecksUtil.checkConnection(requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    homeViewModel.currentWeather.collectLatest { result ->
                        when (result) {
                            is ApiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is ApiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                updateData(result.data)

                                // Check if hourly and daily lists are not empty before submitting
                                homeHourlyAdapter.submitList(result.data.hourly.takeIf { it.isNotEmpty() } ?: emptyList())
                                homeDailyAdapter.submitList(result.data.daily.takeIf { it.isNotEmpty() } ?: emptyList())

                                if (result.data.current.weather.isNotEmpty()) {
                                    when (result.data.current.weather[0].main) {
                                        "Rain", "shower rain" -> binding.weatherViewHome.setWeatherData(PrecipType.RAIN)
                                        "Snow" -> binding.weatherViewHome.setWeatherData(PrecipType.SNOW)
                                        "Clear" -> binding.weatherViewHome.setWeatherData(PrecipType.CLEAR)
                                    }
                                } else {
                                    // Handle empty weather case, e.g., show a default message or icon
                                    binding.weatherViewHome.setWeatherData(PrecipType.CUSTOM) // assuming you have a default type
                                }
                            }

                            is ApiState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                Log.d(TAG, "Exception is: ${result.msg}")
                                Toast.makeText(requireActivity(), result.msg.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    homeViewModel.currentWeatherFromDatabase.collectLatest { result ->
                        binding.progressBar.visibility = View.GONE
                        updateData(result)

                        // Check if current weather list is not empty before accessing it
                        if (result.current.weather.isNotEmpty()) {
                            when (result.current.weather[0].main) {
                                "Rain", "shower rain" -> binding.weatherViewHome.setWeatherData(PrecipType.RAIN)
                                "Snow" -> binding.weatherViewHome.setWeatherData(PrecipType.SNOW)
                                "Clear" -> binding.weatherViewHome.setWeatherData(PrecipType.CLEAR)
                            }
                        } else {
                            // Handle empty weather case, e.g., show a default message or icon
                            binding.weatherViewHome.setWeatherData(PrecipType.CUSTOM) // assuming you have a default type
                        }
                    }
                }
            }
        }
    }


    private fun fetchWeatherData() {


        latitudeFromPrefs =
            locationSharedPreferences.getString(LATITUDE, null)
        longitudeFromPrefs =
            locationSharedPreferences.getString(LONGITUDE, null)

        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "metric")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "en").toString()


        val tempUnit = when (tempUnitFromPrefs) {
            KELVIN -> STANDARD
            FAHRENHEIT -> IMPERIAL
            else -> METRIC

        }
        Log.d(TAG, "Temperature unit updated from preferences: $tempUnit ")

        Log.d(
            TAG,
            "Latitude retrieved from preferences: $latitudeFromPrefs, Longitude fetched from preferences: $longitudeFromPrefs "
        )

        if (latitudeFromPrefs != null && longitudeFromPrefs != null) {
            homeViewModel.setCurrentLocation(
                latitudeFromPrefs!!,
                longitudeFromPrefs!!,
                languageFromPrefs,
                tempUnit
            )
        }
    }


    private fun updateData(weatherResponse: WeatherResponse) {
        val dateTime = weatherResponse.current.dt
        val tempDegree = weatherResponse.current.temp

        // Check if the weather list is not empty before accessing it
        val main = if (weatherResponse.current.weather.isNotEmpty()) {
            weatherResponse.current.weather[0].main
        } else {
            "Unknown"
        }

        val humidity = weatherResponse.current.humidity
        val windSpeed = weatherResponse.current.wind_speed
        val pressure = weatherResponse.current.pressure
        val clouds = weatherResponse.current.clouds

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val address = geocoder.getAddress(
                latitudeFromPrefs?.toDouble()?.round(4) ?: 0.0,
                longitudeFromPrefs?.toDouble()?.round(4) ?: 0.0
            )!!

            val city = address.locality ?: address.extras.getString("sub-admin", "Unknown area")
            Log.d(TAG, "locality $city ")

            binding.cvDetails.visibility = View.VISIBLE
            binding.cvCurrentWeather.visibility = View.VISIBLE
            binding.tvAddress.text = city
            val realDateTime = timestampToDate(dateTime)
            binding.tvDateTime.text = realDateTime

            when (tempUnitFromPrefs) {
                KELVIN -> {
                    binding.tvTempDegree.text = "$tempDegree °K"
                    binding.tvWindSpeedDesc.text = "${windSpeed} meter/sec"
                }

                FAHRENHEIT -> {
                    binding.tvTempDegree.text = "$tempDegree °F"
                    binding.tvWindSpeedDesc.text = "${windSpeed} miles/hour"
                }

                else -> {
                    binding.tvTempDegree.text = "$tempDegree °C"
                    binding.tvWindSpeedDesc.text = "${windSpeed} meter/sec"
                }
            }

            binding.tvMain.text = main
            val humidityUnit = getString(R.string.humidity_unit)
            binding.tvHumidityDesc.text = "$humidity $humidityUnit"
            val pressureUnit = getString(R.string.pressure_unit)
            binding.tvPressureDesc.text = "$pressure $pressureUnit"
            val cloudUnit = getString(R.string.cloud_unit)
            binding.tvCloudsDesc.text = "$clouds $cloudUnit"

            // Fetch and display 5-day forecast
            val forecastList = weatherResponse.daily.take(5) // Get the first 5 days of the forecast
            updateForecastAdapter(forecastList.takeIf { it.isNotEmpty() } ?: emptyList())
        }
    }

    private fun updateForecastAdapter(forecastList: List<DailyForecast>) {
        val adapter = binding.rvDaily.adapter as? HomeDailyAdapter
        adapter?.submitList(forecastList)
    }

    private fun timestampToDate(timeStamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val date = Date(timeStamp * 1000)
        return simpleDateFormat.format(date)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }


    override fun onResume() {
        super.onResume()
        fetchWeatherData()
    }

}


