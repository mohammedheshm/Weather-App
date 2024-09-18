package com.example.weatherapp.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.settings.view.LANG_KEY
import com.example.weatherapp.settings.view.TEMP_UNIT_KEY
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeDailyAdapter(private val context: Context) :
    ListAdapter<DailyForecast, DailyViewHolder>(DailyDiffUtil()) {

    private lateinit var prefsSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String = ""
    private var languageFromPrefs: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.daily_item, parent, false)
        prefsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tempUnitFromPrefs = prefsSharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit") ?: "No saved Temp unit"
        languageFromPrefs = prefsSharedPreferences.getString(LANG_KEY, "No Saved Language") ?: "No Saved Language"
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyForecast: DailyForecast = getItem(position)

        val day = convertTimestampToday(dailyForecast.dt)
        val iconResId = convertIconCodeToResId(dailyForecast.weather[0].icon)

        holder.dayDaily.text = day
        holder.descDaily.text = dailyForecast.weather[0].description
        val max = dailyForecast.temp.max
        val min = dailyForecast.temp.min
        val fullTempDaily = "$max \\ $min"

        holder.tempDaily.text = when (tempUnitFromPrefs) {
            KELVIN -> "$fullTempDaily °K"
            FAHRENHEIT -> "$fullTempDaily °F"
            else -> "$fullTempDaily °C"
        }

        holder.iconDaily.setImageResource(iconResId)
    }
}

private fun convertTimestampToday(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("EEE, dd", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}

private fun convertIconCodeToResId(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.weathericon
        "01n" -> R.drawable.moonicon
        "02d" -> R.drawable.moonicon
        "02n" -> R.drawable.moonicon
        "03d", "03n" -> R.drawable.weathericon
        "04d", "04n" -> R.drawable.moonicon
        "09d", "09n" -> R.drawable.weathericon
        "10d" -> R.drawable.moonicon
        "10n" -> R.drawable.moonicon
        "11d", "11n" -> R.drawable.moonicon
        "13d", "13n" -> R.drawable.weathericon
        "50d", "50n" -> R.drawable.moonicon
        else -> R.drawable.moonicon // Default drawable
    }
}

class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayDaily: TextView = itemView.findViewById(R.id.tv_day_daily)
    val descDaily: TextView = itemView.findViewById(R.id.tv_desc_daily)
    val iconDaily: ImageView = itemView.findViewById(R.id.iv_icon_daily)
    val tempDaily: TextView = itemView.findViewById(R.id.tv_full_temp_daily)
}

class DailyDiffUtil : DiffUtil.ItemCallback<DailyForecast>() {
    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}
