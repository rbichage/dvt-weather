package com.dvt.weatherforecast.ui.search

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.dvt.weatherforecast.BuildConfig
import com.dvt.weatherforecast.data.models.places.CustomPlaceDetails
import com.dvt.weatherforecast.databinding.ActivitySearchBinding
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.view.hideSoftInput
import com.dvt.weatherforecast.utils.view.showErrorDialog
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var placesAutoCompleteAdapter: PlacesAutoCompleteAdapter
    private lateinit var placesClient: PlacesClient
    private var dialog: Dialog? = null

    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializePlaces()
        observeViewModel()
        setupViews()
    }

    private fun observeViewModel() {

        val builder = MaterialAlertDialogBuilder(this)

        dialog = builder.create()

        viewModel.isLoading.observe(this) {
            when (it) {
                true -> {
                    builder.setMessage("please wait")
                    builder.setCancelable(false)
                    dialog?.show()
                }

                false -> {
                    dialog?.dismiss()

                }
            }
        }
    }

    private fun initializePlaces() {
        Places.initialize(this, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)

        val token = AutocompleteSessionToken.newInstance()

        placesAutoCompleteAdapter = PlacesAutoCompleteAdapter(placesClient, token,
                object : PlacesRecyclerListener {
                    override fun requestStarted() {

                    }

                    override fun requestSuccessful() {

                    }

                    override fun placeSelected(place: CustomPlaceDetails) {
                        //save to Db
                        Timber.e("place details $place")
                        getFromLocation(place)
                    }

                    override fun onError(e: Exception) {
                        //show error

                        lifecycleScope.launchWhenStarted {
                            hideSoftInput()
                            showErrorDialog(
                                    message = "Unable to complete your request, try again later",
                                    positiveText = "Retry",
                                    negativeText = "Cancel",
                                    positiveAction = { },
                                    negativeAction = { onBackPressed() }
                            )
                        }

                    }

                    override fun hideKeyboard() {
                        hideSoftInput()
                    }
                })
    }

    private fun getFromLocation(place: CustomPlaceDetails) {
        lifecycleScope.launchWhenStarted {
            val location = Location("This").apply {
                latitude = place.latLng.latitude
                longitude = place.latLng.longitude
            }

            viewModel.getDataFromLocation(location).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        viewModel.insertNewToDb(response.value, place.name)
                        dialog?.dismiss()
                        onBackPressed()
                    }
                    is ApiResponse.Failure -> {
                        dialog?.dismiss()
                        showErrorDialog(
                                message = response.errorHolder.message,
                                positiveText = "Retry",
                                negativeText = "Cancel",
                                positiveAction = { getFromLocation(place) },
                                negativeAction = { onBackPressed() }
                        )
                    }
                }
            }
        }
    }

    private fun setupViews() {
        with(binding.placesRecycler) {
            adapter = placesAutoCompleteAdapter
        }



        binding.etSearch.addTextChangedListener { editable ->
            when {
                editable.toString().length > 3 -> {
                    placesAutoCompleteAdapter.filter.filter(editable.toString())
                }
            }
        }
    }
}