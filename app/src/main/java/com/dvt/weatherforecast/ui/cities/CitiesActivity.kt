package com.dvt.weatherforecast.ui.cities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dvt.weatherforecast.databinding.ActivityFavouritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CitiesActivity : AppCompatActivity() {
    private val binding: ActivityFavouritesBinding by lazy {
        ActivityFavouritesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}