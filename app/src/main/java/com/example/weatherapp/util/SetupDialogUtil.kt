package com.example.weatherapp.util

import com.example.weatherapp.R
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Toast
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

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return AlertDialog.Builder(requireActivity())
//            .setTitle("Set Location Permission")
//            .setPositiveButton("OK") { dialog, which ->
//                positiveButtonListener?.onClick(dialog, which)
//                val selected = initialPrefs.getString(INITIAL_CHOICE, GPS) ?: GPS
//                if (selected == LOCATION) {
//                    val action =
//                        HomeFragmentDirections.actionHomeFragmentToMapFragment3(HOME_FRAGMENT)
//                    findNavController().navigate(action)
//                } else homeViewModel.setCurrentSettings(selected)
//                Log.d(TAG, "selected: $selected ")
//            }
//            .setSingleChoiceItems(
//                arrayOf(GPS, "Location"), 0
//            ) { dialog, which ->
//                val selected = if (which == 0) GPS else LOCATION
//                initialPrefs.edit().putString(INITIAL_CHOICE, selected).commit()
//                if (which == 0) {
//                } else {
//                }
//            }
//            .create()
//
//
//    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_item, null)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_options)
        val buttonOk = dialogView.findViewById<Button>(R.id.button_ok)
        buttonOk.setOnClickListener {

            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                // No option selected, show a toast
                Toast.makeText(requireContext(), "You need to choose an option", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener // Stop execution
            }

            val selectedOption = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_gps -> GPS
                R.id.radio_location -> LOCATION
                else -> GPS // Default option
            }

            // Save the selection and perform any necessary actions
            initialPrefs.edit().putString(INITIAL_CHOICE, selectedOption).apply()
            Log.d(TAG, "Selected: $selectedOption")

            // Navigate or perform actions based on selection
            if (selectedOption == LOCATION) {
                val action = HomeFragmentDirections.actionHomeFragmentToMapFragment3(HOME_FRAGMENT)
                findNavController().navigate(action)
            } else {
                homeViewModel.setCurrentSettings(selectedOption)
            }

            dismiss() // Close the dialog
        }

        return AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
    }
}




