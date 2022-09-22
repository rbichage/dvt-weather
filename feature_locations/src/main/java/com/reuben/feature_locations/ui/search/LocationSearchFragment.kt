package com.reuben.feature_locations.ui.search

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reuben.core_ui.binding.viewBinding
import com.reuben.core_ui.hide
import com.reuben.core_ui.hideSoftInput
import com.reuben.core_ui.show
import com.reuben.core_ui.toast
import com.reuben.feature_locations.data.places.CustomAddressModel
import com.reuben.feature_locations.databinding.FragmentSearchLocationsBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LocationSearchFragment : BottomSheetDialogFragment() {
    private val binding by viewBinding(FragmentSearchLocationsBinding::bind)

    private val viewModel: SearchViewModel by viewModels()

    private val placesClient: PlacesClient by lazy {
        return@lazy Places.createClient(binding.root.context)
    }

    private val autoCompleteToken by lazy {
        AutocompleteSessionToken.newInstance()
    }


    private val placesAutoCompleteAdapter: PlacesAutoCompleteAdapter by lazy {
        PlacesAutoCompleteAdapter(
                onPlaceSelected = {
                    activity?.hideSoftInput()
                    getLocationFromAddress(it)
                }
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet =
                    dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)


            val params = bottomSheet.layoutParams
            val height: Int = Resources.getSystem().displayMetrics.heightPixels
            params.height = height
            bottomSheet.layoutParams = params


            bottomSheet.setBackgroundColor(Color.WHITE)

            val behaviour = BottomSheetBehavior.from(bottomSheet)

            behaviour.apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isDraggable = true
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupViews()
    }

    private fun observeViewModel() {


        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { uistate ->
                when (uistate) {
                    is SearchUIState.Addresses -> {
                        isCancelable = true
                        submitAddresses(uistate.addresses)
                    }
                    is SearchUIState.Error -> {
                        showViews()
                        isCancelable = true
                    }
                    SearchUIState.Loading -> {
                        isCancelable = false
                        with(binding) {
                            loadingProgress.show()
                            tvLoadingStatus.show()
                            tvLoadingStatus.text = "Loading...please wait"
                            placesRecycler.hide()
                            locationLayout.hide()
                        }
                    }
                    SearchUIState.Init -> {}
                    is SearchUIState.GeocodeData -> {
                        val customPlace = uistate.customPlaceDetails.latLng
                        val location = Location(javaClass.name).apply {
                            latitude = customPlace.latitude
                            longitude = customPlace.longitude
                        }
                        viewModel.getWeatherDataFromLocation(location, uistate.customPlaceDetails.name)
                    }
                    SearchUIState.NoAddress -> {
                        showViews()
                        activity?.toast("Oopsie! No address")
                    }
                    SearchUIState.WeatherAdded -> {
                        dismiss()
                    }
                }
            }
        }

    }

    private fun showViews() {
        with(binding) {
            isCancelable = true
            loadingProgress.hide()
            tvLoadingStatus.hide()
            placesRecycler.show()
            locationLayout.show()
        }
    }

    private fun setupViews() {
        with(binding.placesRecycler) {
            adapter = placesAutoCompleteAdapter
        }


        binding.etSearch.doAfterTextChanged { editable ->

            val query = editable?.toString().orEmpty()
            Timber.e("query")
            when {
                query.length > 2 -> {
                    viewModel.getPlaceName(
                            placesClient = placesClient,
                            token = autoCompleteToken,
                            placeQuery = query
                    )
                }
            }
        }
    }

    private fun submitAddresses(data: List<CustomAddressModel>) {
        if (data.isNotEmpty()) {
            placesAutoCompleteAdapter.submitList(data)
        } else {
            activity?.toast("no results found")
        }
    }


    private fun getLocationFromAddress(address: CustomAddressModel) {
        viewModel.reverseGeoCodeLocation(address)
    }


}