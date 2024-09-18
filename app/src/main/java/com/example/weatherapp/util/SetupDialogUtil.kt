package com.example.weatherapp.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.home.view.HomeFragmentDirections
import com.example.weatherapp.home.viewmodel.HomeViewModel


const val INITIAL_PREFS = "InitialPreferences"
const val INITIAL_CHOICE = "initialChoice"
const val HOME_FRAGMENT = "home_fragment"
const val GPS = "GPS"
const val LOCATION = "Location"

class SetupDialogUtil : DialogFragment() {

    private val TAG = "InitialSetupDialog"

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    lateinit var initialPrefs: SharedPreferences
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialPrefs = requireActivity().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())


    }

    fun setPositiveButton(listener: DialogInterface.OnClickListener) {
        positiveButtonListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Initial Setup")
            .setPositiveButton("OK") { dialog, which ->
                positiveButtonListener?.onClick(dialog, which)
                val selected = initialPrefs.getString(INITIAL_CHOICE, GPS) ?: GPS
                if (selected == LOCATION) {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToMapFragment3(HOME_FRAGMENT)
                    findNavController().navigate(action)
                } else homeViewModel.setCurrentSettings(selected)
                Log.d(TAG, "selected: $selected ")
            }
            .setSingleChoiceItems(
                arrayOf(GPS, "Location"), 0
            ) { dialog, which ->
                val selected = if (which == 0) GPS else LOCATION
                initialPrefs.edit().putString(INITIAL_CHOICE, selected).commit()
                if (which == 0) {
                } else {
                }
            }
            .create()


    }
}



