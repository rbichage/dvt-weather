package com.reuben.feature_locations.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reuben.core_ui.binding.viewBinding
import com.reuben.feature_locations.data.places.CustomAddressModel
import com.reuben.feature_locations.databinding.LayoutPlaceResultItemBinding

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
                parent.viewBinding(LayoutPlaceResultItemBinding::inflate)
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