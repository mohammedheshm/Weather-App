package com.example.weatherapp.home.view

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.HourlyForecast
import com.example.weatherapp.settings.view.LANG_KEY
import com.example.weatherapp.settings.view.TEMP_UNIT_KEY
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeHourlyAdapter(private val context: Context) :
    ListAdapter<HourlyForecast, HourlyViewHolder>(HourlyDiffUtil()) {

    private lateinit var prefsSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.hourly_item, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyForecast: HourlyForecast = getItem(position)

        prefsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tempUnitFromPrefs = prefsSharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit")
        languageFromPrefs = prefsSharedPreferences.getString(LANG_KEY, "No Saved Language").toString()

        val time = convertTimestampToDate(hourlyForecast.dt)
        val iconResId = convertIconCodeToResId(hourlyForecast.weather[0].icon)
        holder.timeHourly.text = time
        val tempDegree = hourlyForecast.temp.toString()

        holder.tempHourly.text = when (tempUnitFromPrefs) {
            KELVIN -> "$tempDegree °K"
            FAHRENHEIT -> "$tempDegree °F"
            else -> "$tempDegree °C"
        }

        holder.iconHourly.setImageResource(iconResId)
    }
}

private fun convertTimestampToDate(timeStamp: Long): String {
    val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = Date(timeStamp * 1000)
    return simpleDateFormat.format(date)
}

private fun convertIconCodeToResId(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.weathericon
        "01n" -> R.drawable.moonicon
        "02d" -> R.drawable.weathericon
        "02n" -> R.drawable.moonicon
        "03d", "03n" -> R.drawable.weathericon
        "04d", "04n" -> R.drawable.moonicon
        "09d", "09n" -> R.drawable.weathericon
        "10d" -> R.drawable.moonicon
        "10n" -> R.drawable.moonicon
        "11d", "11n" -> R.drawable.weathericon
        "13d", "13n" -> R.drawable.weathericon
        "50d", "50n" -> R.drawable.moonicon
        else -> R.drawable.moonicon // Default drawable
    }
}

class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val timeHourly: TextView = itemView.findViewById(R.id.tv_time_hourly)
    val iconHourly: ImageView = itemView.findViewById(R.id.iv_icon_hourly)
    val tempHourly: TextView = itemView.findViewById(R.id.tv_temp_hourly)
}

class HourlyDiffUtil : DiffUtil.ItemCallback<HourlyForecast>() {
    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem == newItem
    }
}
