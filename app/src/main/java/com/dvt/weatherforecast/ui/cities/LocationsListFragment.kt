package com.dvt.weatherforecast.ui.cities

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.FragmentCitiesListBinding
import com.dvt.weatherforecast.ui.home.HomeActivity
import com.dvt.weatherforecast.ui.search.SearchActivity
import com.dvt.weatherforecast.utils.view.navigateTo
import com.dvt.weatherforecast.utils.view.showErrorSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

        locationsAdapter = LocationsAdapter(object : OnItemSelected {
            override fun onClick(locationEntity: LocationEntity) {

                Intent(binding.root.context, HomeActivity::class.java).apply {
                    putExtra("locationEntity", locationEntity)
                    activity?.setResult(RESULT_OK, this)
                    activity?.finish()
                }

            }

            override fun onLongClick(locationEntity: LocationEntity) {

                if (locationEntity.isCurrent) {
                    binding.root.showErrorSnackbar("You cannot delete current location", Snackbar.LENGTH_LONG)
                } else {

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
            }

        })

        with(binding.citiesRecycler) {
            adapter = locationsAdapter
        }

        binding.btnAddLocation.setOnClickListener {
            activity?.navigateTo<SearchActivity>(false) { }
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