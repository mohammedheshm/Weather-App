package com.example.weatherapp.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R

class SplashScreenActivity : AppCompatActivity() {

    lateinit var getStart_btn: Button
    lateinit var sp_imageView: ImageView
    lateinit var sp_weatherTv: TextView
    lateinit var sp_forecastTV: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        sp_imageView = findViewById(R.id.sp_img)
        sp_weatherTv = findViewById(R.id.sp_weatherTV)
        sp_forecastTV = findViewById(R.id.sp_forecastTV)
        getStart_btn = findViewById(R.id.sp_getStart_btn)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val translate = AnimationUtils.loadAnimation(this, R.anim.translate)

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleUp)
        animationSet.addAnimation(translate)
        animationSet.duration = 1500

        sp_imageView.startAnimation(animationSet)
        sp_weatherTv.startAnimation(fadeIn)
        sp_forecastTV.startAnimation(fadeIn)
        getStart_btn.startAnimation(fadeIn)

        getStart_btn.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}