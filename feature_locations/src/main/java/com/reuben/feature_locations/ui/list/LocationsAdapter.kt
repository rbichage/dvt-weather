package com.reuben.feature_locations.ui.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_ui.show
import com.reuben.feature_locations.databinding.LayoutCityItemBinding
import timber.log.Timber

typealias onLocationSelected = (LocationEntity) -> Unit
typealias onLongClick = (LocationEntity) -> Unit

class LocationsAdapter(private val onLocationSelected: onLocationSelected, private val onLongClick: onLongClick) :
        ListAdapter<LocationEntity, LocationsAdapter.LocationViewHolder>(diffUtil) {
    inner class LocationViewHolder(private val binding: LayoutCityItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: LocationEntity) {
            Timber.e("item details $item")
            with(binding) {
                if (item.isCurrent) button.show()
                tvCityName.text = item.name
                tvTemp.text = "${item.normalTemp} ℃"
                tvCondition.text = item.weatherConditionName

                root.setOnClickListener { onLocationSelected.invoke(item) }

                root.setOnLongClickListener {
                    onLongClick.invoke(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder =
            LocationViewHolder(
                    LayoutCityItemBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    )
            )

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

val diffUtil = object : DiffUtil.ItemCallback<LocationEntity>() {
    override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
        return oldItem.lat == newItem.lat
    }

    override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
        return oldItem == newItem
    }


}