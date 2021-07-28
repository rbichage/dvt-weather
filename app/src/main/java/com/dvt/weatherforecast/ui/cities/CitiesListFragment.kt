package com.dvt.weatherforecast.ui.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dvt.weatherforecast.databinding.FragmentCitiesListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CitiesListFragment : Fragment() {

    private val binding: FragmentCitiesListBinding by lazy {
        FragmentCitiesListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

}