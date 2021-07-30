package com.dvt.weatherforecast.ui.cities

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.databinding.ActivityFavouritesBinding
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LocationsActivity : AppCompatActivity() {
    private val binding: ActivityFavouritesBinding by lazy {
        ActivityFavouritesBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.location_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }

        addTabsListener()


    }

    private fun addTabsListener() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { tabLayout ->
                    when (tabLayout.position) {
                        0 -> {

                            val action = MapFragmentDirections.actionMapFragmentToCitiesListFragment()
                            navController.navigate(action)

                        }

                        1 -> {

                            if (isLocationPermissionEnabled()) {
                                val action = LocationsListFragmentDirections.actionCitiesListFragmentToMapFragment()
                                navController.navigate(action)


                            } else {
                                requestLocationPermission()

                            }

                        }
                        else -> {
                            Timber.e("Nothing to display")
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.e("tab reselected")
            }

        })
    }

    private fun requestLocationPermission() {
        val permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )

        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // do nothing for now
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                        val snackBar = Snackbar.make(
                                binding.root,
                                "Enable location to continue",
                                Snackbar.LENGTH_INDEFINITE
                        )

                        snackBar.apply {
                            setAction("Enable") {
                                snackBar.dismiss()
                                requestLocationPermission()
                            }
                            show()
                        }
                    }

                }).check()
    }
}