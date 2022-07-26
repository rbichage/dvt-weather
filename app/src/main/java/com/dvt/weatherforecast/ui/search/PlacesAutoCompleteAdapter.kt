package com.dvt.weatherforecast.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherforecast.data.models.places.CustomAddressModel
import com.dvt.weatherforecast.databinding.LayoutPlaceResultItemBinding

class PlacesAutoCompleteAdapter(
        private val onPlaceSelected: (CustomAddressModel) -> Unit
) : ListAdapter<CustomAddressModel, PlacesAutoCompleteAdapter.ViewHolder>(diffUtil) {


    inner class ViewHolder(private val binding: LayoutPlaceResultItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomAddressModel) {
            with(binding) {
                tvLocationName.text = item.name
                tvLocationAddress.text = item.address

                root.setOnClickListener {
                    onPlaceSelected(item)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

private val diffUtil = object : DiffUtil.ItemCallback<CustomAddressModel>() {
    override fun areItemsTheSame(oldItem: CustomAddressModel, newItem: CustomAddressModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: CustomAddressModel, newItem: CustomAddressModel): Boolean {
        return oldItem == newItem
    }

}