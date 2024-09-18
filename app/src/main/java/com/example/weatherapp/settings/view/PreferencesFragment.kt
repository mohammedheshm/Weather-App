package com.example.weatherapp.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.R

const val TEMP_UNIT_KEY = "temp_unit_key"
const val LOCATION_GPS_KEY = "location_gps"
const val WIND_UNIT_KEY = "wind_unit_key"
const val LANG_KEY = "Lang_key"
const val PREFERENCES_FRAGMENT = "preferences_fragment"

class PreferencesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }

}