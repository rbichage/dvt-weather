package com.reuben.feature_locations.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reuben.core_ui.binding.viewBinding
import com.reuben.core_ui.toast
import com.reuben.feature_locations.R
import com.reuben.feature_locations.databinding.FragmentLocationListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LocationsListFragment : Fragment(R.layout.fragment_location_list) {

    private val binding by viewBinding(FragmentLocationListBinding::bind)

    private val viewModel: LocationsListViewModel by viewModels()
    private val locationsAdapter by lazy {
        LocationsAdapter(
                onLocationSelected = { locationEntity ->
                    activity?.toast(locationEntity.name)
                    //TODO: Pass this to weather fragment

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

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        getItems()
    }

    private fun setupViews() {

        with(binding.citiesRecycler) {
            adapter = locationsAdapter
        }

        binding.btnAddLocation.setOnClickListener {
//            LocationsListFragmentDirections.toLocationSearch().also {
//                findNavController().navigate(it)
//            }
        }
    }

    private fun getItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLocations().collectLatest { locations ->
                    Timber.e("locations $locations")

                    if (locations.isNotEmpty()) {
                        locationsAdapter.submitList(locations)
                    }
                }
            }

        }
    }

}