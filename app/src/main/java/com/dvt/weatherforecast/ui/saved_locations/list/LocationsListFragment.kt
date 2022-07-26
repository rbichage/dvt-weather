package com.dvt.weatherforecast.ui.saved_locations.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dvt.weatherforecast.databinding.FragmentCitiesListBinding
import com.dvt.weatherforecast.ui.saved_locations.LocationsAdapter
import com.dvt.weatherforecast.ui.saved_locations.LocationsViewModel
import com.dvt.weatherforecast.utils.view.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LocationsListFragment : Fragment() {

    private val binding: FragmentCitiesListBinding by lazy {
        FragmentCitiesListBinding.inflate(layoutInflater)
    }

    private val viewModel: LocationsViewModel by viewModels()
    private lateinit var locationsAdapter: LocationsAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        getItems()
    }

    private fun setupViews() {

        locationsAdapter = LocationsAdapter(
                onLocationSelected = { locationEntity ->
                    activity?.toast(locationEntity.name)
                },

                onLongClick = { locationEntity ->
                    MaterialAlertDialogBuilder(binding.root.context)
                            .setTitle("DELETE")
                            .setMessage("Do you wish to delete ${locationEntity.name}?")
                            .setPositiveButton("YES") { dialog, _ ->
                                viewModel.deleteEntity(locationEntity)
                                dialog.dismiss()
                            }
                            .setNegativeButton("CANCEL") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                }
        )

        with(binding.citiesRecycler) {
            adapter = locationsAdapter
        }

        binding.btnAddLocation.setOnClickListener {
            LocationsListFragmentDirections.toLocationSearch().also {
                findNavController().navigate(it)
            }
        }
    }

    private fun getItems() {
        lifecycleScope.launchWhenStarted {
            viewModel.getLocations().collect { locations ->
                Timber.e("locations $locations")

                if (locations.isNotEmpty()) {
                    locationsAdapter.submitList(locations)
                }
            }
        }
    }

}