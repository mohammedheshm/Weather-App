package com.example.weatherapp.settings.view

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.weatherapp.R
import com.example.weatherapp.util.PermissionChecksUtil

const val PREFERENCES_FRAGMENT = "preferences_fragment"
const val TEMP_UNIT_KEY = "temp_unit"
const val LOCATION_GPS_KEY = "location_gps"
const val WIND_UNIT_KEY = "wind_unit"
const val LANG_KEY = "language"

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Handle Language Preference
        val languagePreference = findPreference<ListPreference>(LANG_KEY)
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            UpdateLanguage(newValue.toString())
            true
        }

        // Handle Location by GPS Switch
        val locationGpsSwitch = findPreference<SwitchPreference>(LOCATION_GPS_KEY)
        locationGpsSwitch?.setOnPreferenceChangeListener { _, isEnabled ->
            if (isEnabled as Boolean) {
                Toast.makeText(requireContext(), "GPS enabled", Toast.LENGTH_SHORT).show()
                // Add GPS activation code here
            } else {
                Toast.makeText(requireContext(), "GPS disabled", Toast.LENGTH_SHORT).show()
                // Add GPS deactivation code here
            }
            true
        }

        // Handle Location Map Selection
        val locationMapPreference: Preference? = findPreference("location_map")
        locationMapPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (PermissionChecksUtil.checkConnection(requireContext())) {
                val action = PreferencesFragmentDirections.actionPreferencesFragmentToMapFragment(
                    PREFERENCES_FRAGMENT
                )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }

        // Handle Temperature Unit List Preference
        val tempUnitPreference = findPreference<ListPreference>(TEMP_UNIT_KEY)
        tempUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
            Toast.makeText(requireContext(), "Temperature Unit: $newValue", Toast.LENGTH_SHORT)
                .show()
            true
        }

        // Handle Wind Speed Unit List Preference
        val windUnitPreference = findPreference<ListPreference>(WIND_UNIT_KEY)
        windUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
            Toast.makeText(requireContext(), "Wind Speed Unit: $newValue", Toast.LENGTH_SHORT)
                .show()
            true
        }
    }

    // Function to handle enabling GPS
    private fun enableGPS() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, show prompt to enable it
            Toast.makeText(requireContext(), "Enabling GPS", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            // GPS is already enabled
            Toast.makeText(requireContext(), "GPS is already enabled", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle disabling GPS (redirects to location settings)
    private fun disableGPS() {
        // Note: Apps cannot programmatically disable GPS. Show a message or redirect to settings
        Toast.makeText(requireContext(), "Please disable GPS manually", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    private fun UpdateLanguage(language: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language)
        )
    }
}