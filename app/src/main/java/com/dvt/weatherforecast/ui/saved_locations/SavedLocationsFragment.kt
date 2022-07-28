package com.dvt.weatherforecast.ui.saved_locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.databinding.FragmentSavedLocationsBinding
import com.google.android.material.tabs.TabLayout
import timber.log.Timber

class SavedLocationsFragment : Fragment(R.layout.fragment_saved_locations) {

    private val binding by lazy {
        FragmentSavedLocationsBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback {
            parentFragment?.findNavController()?.navigate(SavedLocationsFragmentDirections.toHomeFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.location_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        addTabsListener()
    }

    private fun addTabsListener() {

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { tabLayout ->
                    when (tabLayout.position) {
                        0 -> {

                            navController.navigate(R.id.locationsListFragment)

                        }

                        1 -> {

                            navController.navigate(R.id.mapFragment)
                        }
                        else -> {
                            Timber.e("Nothing to display")
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Timber.e("$tab unselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.e("tab reselected")
            }

        })
    }
}