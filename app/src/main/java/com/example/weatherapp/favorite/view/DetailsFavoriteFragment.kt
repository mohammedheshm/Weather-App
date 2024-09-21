package com.example.weatherapp.favorite.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentDetailsFavoriteBinding
import com.example.weatherapp.db.WeatherLocalDataSourceImpl
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherapp.home.view.HomeDailyAdapter
import com.example.weatherapp.home.view.HomeHourlyAdapter
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepoImpl
import com.example.weatherapp.network.ApiState
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.weatherapp.util.getAddress
import com.example.weatherapp.util.round
import com.example.weatherapp.util.PermissionChecksUtil

class DetailsFavoriteFragment : Fragment() {

    private val TAG = "DetailsFavoriteFragment"
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var homeDailyAdapter: HomeDailyAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tempUnitFromPrefs: String? = "metric"
    private var languageFromPrefs: String = "en"
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null

    private lateinit var binding: FragmentDetailsFavoriteBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.rvHourly.adapter = homeHourlyAdapter
        binding.rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        binding.rvDaily.adapter = homeDailyAdapter
        binding.rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if (PermissionChecksUtil.checkPermission(requireContext())) {
            if (PermissionChecksUtil.isLocationIsEnabled(requireContext())) {
                fetchCurrentLocation()
            } else {
                PermissionChecksUtil.enableLocationService(requireContext())
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun fetchCurrentLocation() {
        if (PermissionChecksUtil.checkPermission(requireContext())) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitudeFromPrefs = it.latitude.toString()
                    longitudeFromPrefs = it.longitude.toString()
                    favoriteViewModel.setFavDetailsLocation(
                        latitudeFromPrefs!!,
                        longitudeFromPrefs!!,
                        languageFromPrefs,
                        tempUnitFromPrefs!!
                    )
                    observeWeatherDetails()
                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to retrieve location", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Location permission is not granted",
                Toast.LENGTH_SHORT
            ).show()
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitudeFromPrefs = it.latitude.toString()
                longitudeFromPrefs = it.longitude.toString()

                // Set weather location
                favoriteViewModel.setFavDetailsLocation(
                    latitudeFromPrefs!!,
                    longitudeFromPrefs!!,
                    languageFromPrefs,
                    tempUnitFromPrefs!!
                )

                // Observe and update UI with weather data
                observeWeatherDetails()
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to retrieve location", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun observeWeatherDetails() {
        lifecycleScope.launch {
            favoriteViewModel.favWeatherDetails.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        updateData(result.data)
                        homeHourlyAdapter.submitList(result.data.hourly)
                        homeDailyAdapter.submitList(result.data.daily)

                        when (result.data.current.weather[0].main) {
                            "Rain" -> binding.weatherViewFavDetails.setWeatherData(PrecipType.RAIN)
                            "Snow" -> binding.weatherViewFavDetails.setWeatherData(PrecipType.SNOW)
                            "Clear" -> binding.weatherViewFavDetails.setWeatherData(PrecipType.CLEAR)
                        }
                    }

                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d(TAG, "Exception is: ${result.msg}")
                        Toast.makeText(requireActivity(), result.msg.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun updateData(weatherResponse: WeatherResponse) {
        val dateTime = weatherResponse.current.dt
        val tempDegree = weatherResponse.current.temp
        val main = weatherResponse.current.weather[0].main
        val humidity = weatherResponse.current.humidity
        val windSpeed = weatherResponse.current.wind_speed
        val pressure = weatherResponse.current.pressure
        val clouds = weatherResponse.current.clouds

        // Fetch address using geocoder
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        viewLifecycleOwner.lifecycleScope.launch {
            val address = geocoder.getAddress(
                latitudeFromPrefs?.toDouble()?.round(4) ?: 0.0,
                longitudeFromPrefs?.toDouble()?.round(4) ?: 0.0
            )!!

            val city = address.locality ?: address.extras.getString("sub-admin", "Unknown area")
            binding.cvDetails.visibility = View.VISIBLE
            binding.tvAddress.text = city
            val realDateTime = timestampToDate(dateTime)
            binding.tvDateTime.text = realDateTime

            binding.tvTempDegree.text = "$tempDegree °C"
            binding.tvMain.text = main
            binding.tvHumidityDesc.text = "$humidity %"
            binding.tvWindSpeedDesc.text = "$windSpeed meter/sec"
            binding.tvPressureDesc.text = "$pressure hPa"
            binding.tvCloudsDesc.text = "$clouds %"

            // Update daily forecast
            val forecastList = weatherResponse.daily.take(5)
            updateForecastAdapter(forecastList)
        }
    }

    private fun updateForecastAdapter(forecastList: List<DailyForecast>) {
        val adapter = binding.rvDaily.adapter as? HomeDailyAdapter
        adapter?.submitList(forecastList)
    }

    private fun timestampToDate(timeStamp: Long): String {
        val simpleDateFormat = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val date = java.util.Date(timeStamp * 1000)
        return simpleDateFormat.format(date)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation() // Permission granted, fetch location
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
