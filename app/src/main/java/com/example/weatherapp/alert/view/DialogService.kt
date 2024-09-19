package com.example.weatherapp.alert.view

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.db.WeatherLocalDataSourceImpl
import com.example.weatherapp.home.view.LATITUDE
import com.example.weatherapp.home.view.LOCATION
import com.example.weatherapp.home.view.LONGITUDE
import com.example.weatherapp.model.repository.WeatherRepo
import com.example.weatherapp.model.repository.WeatherRepoImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.example.weatherapp.settings.view.LANG_KEY
import com.example.weatherapp.settings.view.TEMP_UNIT_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogService : Service() {

    private val TAG = "DialogService"
    private var alertType: String? = ""
    private lateinit var _repo: WeatherRepo
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private var description: String? = ""

    override fun onBind(p0: Intent?): IBinder? {

        return null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        alertType = intent?.getStringExtra(ALERT_TYPE)
        Log.d(TAG, "alert type: $alertType ")

        if (alertType == "Alarm") {

            //i will Hnadle This Function to Show Dialog Of Alert on the Screen
        } else if (alertType == "Notification") {


            showNotification(this)
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        _repo = WeatherRepoImpl.getInstance(
            WeatherRemoteDataSourceImpl(),
            WeatherLocalDataSourceImpl(this)
        )

        locationSharedPreferences =
            this.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)
        prefsSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)


        latitudeFromPrefs =
            locationSharedPreferences.getString(LATITUDE, null)
        longitudeFromPrefs =
            locationSharedPreferences.getString(LONGITUDE, null)

        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "metric")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "en").toString()


    }

    private fun showNotification(context: Context) {

        CoroutineScope(Dispatchers.Main).launch {
            _repo.getCurrentWeatherFromRemote(
                latitudeFromPrefs.toString(),
                longitudeFromPrefs.toString(),
                languageFromPrefs,
                tempUnitFromPrefs.toString()
            )
                .collectLatest { result ->
                    val greatWeather = "Great Weather Today, Enjoy!"
                    description =
                        if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
                            greatWeather
                        } else {
                            result.alerts?.get(0)?.description ?: greatWeather
                        }
                    Log.d(TAG, "Notification description: $description ")
                }

            withContext(Dispatchers.Main) {

                val bigTextStyle = NotificationCompat.BigTextStyle().bigText(description)
                val appIntent = Intent(context, MainActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Alert")
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle)
                    .setContentIntent(pendingIntent)
                    .build()

                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)

            }
        }


    }


}