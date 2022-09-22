package com.reuben.feature_locations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.reuben.core_ui.binding.viewBinding
import com.reuben.feature_locations.databinding.FragmentSavedLocationsBinding
import timber.log.Timber

class SavedLocationsFragment : Fragment(R.layout.fragment_saved_locations) {

    private val binding by viewBinding(FragmentSavedLocationsBinding::bind)

    private lateinit var navController: NavController

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
                Timber.d("$tab unselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.d("tab reselected")
            }

        })
    }
}