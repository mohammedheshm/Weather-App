package com.example.weatherapp.alert.view

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
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

class AlarmAndNotificationService : Service() {

    private val TAG = "DialogService"
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ConstraintLayout
    private var desc: String? = ""
    private lateinit var _repo: WeatherRepo
    private lateinit var tvDesc: TextView
    private var alertType: String? = ""
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        alertType = intent?.getStringExtra(ALERT_TYPE)
        Log.d(TAG, "alert type: $alertType ")

        if (alertType == "Alarm") {

            showAlarmDialog()

        } else if (alertType == "Notification") {


            showNotification(this)
        }


        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        initSoundPool()  // Initialize SoundPool

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
                    val greatWeather = "Enjoy The Nice Weather!"
                    desc =
                        if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
                            greatWeather
                        } else {
                            result.alerts?.get(0)?.description ?: greatWeather
                        }
                    Log.d(TAG, "description from notification: $desc ")
                }

            withContext(Dispatchers.Main) {

                val bigTextStyle = NotificationCompat.BigTextStyle().bigText(desc)
                val appIntent = Intent(context, MainActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Alert")
                    .setContentText(desc)
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


    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load the sound
        soundId = soundPool.load(this, R.raw.alert, 1) // Ensure this points to your sound file
    }


    private fun playAlarmSound() {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }


    private fun showAlarmDialog() {
        floatingView = LayoutInflater.from(this)
            .inflate(R.layout.alert_screen_item, null) as ConstraintLayout

        tvDesc = floatingView.findViewById<TextView>(R.id.textView)

        val _repo = WeatherRepoImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(),
            WeatherLocalDataSourceImpl(this)
        )

        CoroutineScope(Dispatchers.Main).launch {
            _repo.getCurrentWeatherFromRemote(
                latitudeFromPrefs.toString(),
                longitudeFromPrefs.toString(),
                languageFromPrefs,
                tempUnitFromPrefs.toString()
            ).collectLatest { result ->
                val greatWeather = "Enjoy The Nice Weather!"
                desc =
                    if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
                        greatWeather
                    } else {
                        result.alerts?.get(0)?.description ?: greatWeather
                    }

                tvDesc.text = desc + " " // Set text to TextView

                // Now create a clickable button inside the TextView
                val buttonText = "Dismiss"
                val spannable = SpannableString("$desc $buttonText")
                val buttonSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // Dismiss the alert when button is clicked
                        windowManager.removeView(floatingView)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true // Optional: underline the text
                        ds.color = Color.BLUE // Optional: change text color
                    }
                }

                spannable.setSpan(
                    buttonSpan,
                    spannable.length - buttonText.length,
                    spannable.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                tvDesc.text = spannable
                tvDesc.movementMethod = LinkMovementMethod.getInstance() // Make TextView clickable

                // Play the alarm sound when the dialog is shown
                playAlarmSound()
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        // The touch listener can be left out if you want to dismiss it only with the button
        floatingView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                stopSelf()
                return@setOnTouchListener true
            }
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager.removeView(floatingView)

        }

        soundPool.release() // Release SoundPool resources

    }

}
