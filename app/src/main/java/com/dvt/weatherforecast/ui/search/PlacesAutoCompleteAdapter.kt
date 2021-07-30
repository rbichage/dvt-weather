package com.dvt.weatherforecast.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherforecast.data.models.places.CustomAddressModel
import com.dvt.weatherforecast.data.models.places.CustomPlaceDetails
import com.dvt.weatherforecast.databinding.LayoutPlaceResultItemBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlacesAutoCompleteAdapter(
        private val placesClient: PlacesClient,
        private val token: AutocompleteSessionToken,
        private val placesRecyclerListener: PlacesRecyclerListener
) : RecyclerView.Adapter<PlacesAutoCompleteAdapter.ViewHolder>(), Filterable {
    var addressList: ArrayList<CustomAddressModel> = ArrayList()
    var returnedList: ArrayList<CustomAddressModel>? = ArrayList()
    private var placesIds: MutableList<String> = ArrayList()


    inner class ViewHolder(private val binding: LayoutPlaceResultItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomAddressModel) {
            with(binding) {
                tvLocationName.text = item.name
                tvLocationAddress.text = item.address

                root.setOnClickListener {
                    placesRecyclerListener.requestStarted()
                    placesRecyclerListener.hideKeyboard()
                    addressList.clear()
                    notifyDataSetChanged()

                    geoCodeAddress(item.name, item.address, binding.root.context)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutPlaceResultItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                )
        )
    }

    override fun getItemCount(): Int = addressList.size


    fun getLocationFromPlace(query: CharSequence): ArrayList<CustomAddressModel>? {
        val autoCompleteList: ArrayList<CustomAddressModel> = ArrayList()

        val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query.toString())
                .setSessionToken(token)


        val results = placesClient.findAutocompletePredictions(request.build()) //call goes here

        placesRecyclerListener.requestStarted()

        try {
            Tasks.await(results, 60, TimeUnit.SECONDS)
        } catch (e: Exception) {
            placesRecyclerListener.onError(e)
            e.printStackTrace()
        }

        return if (results.isSuccessful) {
            Timber.e("getLocationFromPlace: results are ${results.result?.autocompletePredictions}")

            results.result?.let { it ->
                for (prediction in it.autocompletePredictions) {
                    placesIds.add(prediction.placeId)
                    autoCompleteList.add(
                            CustomAddressModel(
                                    prediction.getPrimaryText(null).toString(),
                                    prediction.getSecondaryText(null).toString()
                            )
                    )
                    Timber.e(
                            "getLocationFromPlace: place name ${prediction.getPrimaryText(null)}, secondary text ${
                                prediction.getSecondaryText(
                                        null
                                )
                            }"
                    )
                }
            }

            autoCompleteList
        } else {
            null
        }

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(addressList[position])

    }

    private fun geoCodeAddress(name: String, address: String, context: Context) {

        try {
            val geoCoder = Geocoder(context)

            val results = geoCoder.getFromLocationName("$name, $address", 1)

            Timber.e("onBindViewHolder: results: $results")

            if (results.isNotEmpty()) {
                Timber.e(" onLocationResult: addresses $results")
                val lat = results[0].latitude
                val lng = results[0].longitude
                val customPlaceDetails = CustomPlaceDetails(name, address, LatLng(lat, lng))
                placesRecyclerListener.placeSelected(customPlaceDetails)

            }
        } catch (exception: IOException) {
            exception.printStackTrace()
            placesRecyclerListener.onError(exception)
        }


    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()


                constraint?.let {
                    returnedList = getLocationFromPlace(constraint)
                    Timber.e("performFiltering: list size ${returnedList?.size}")
                }

                filterResults.apply {
                    values = returnedList
                }

                returnedList?.let {
                    filterResults.count = it.size
                }

                return filterResults
            }

            @SuppressLint("UncheckedCast")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Timber.e("publishResults: results ${results?.count}")
                placesRecyclerListener.requestSuccessful()

                if (results != null && results.count > 0) {
                    addressList = results.values as ArrayList<CustomAddressModel>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetChanged()
                }

            }

        }
    }
}