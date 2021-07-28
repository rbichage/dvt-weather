package com.dvt.weatherforecast

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.dvt.weatherforecast.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val colorDrawable: ColorDrawable = binding.root.background as ColorDrawable

        val background = colorDrawable.color


    }

    private fun setStatusBarColor(color: Int) {
        window.statusBarColor = color
    }
}