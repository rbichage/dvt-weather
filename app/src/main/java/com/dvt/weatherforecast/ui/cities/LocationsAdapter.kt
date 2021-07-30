package com.dvt.weatherforecast.ui.cities

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.LayoutCityItemBinding
import com.dvt.weatherforecast.utils.view.show
import timber.log.Timber

class LocationsAdapter(private val onItemSelected: OnItemSelected) :
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

                root.setOnClickListener { onItemSelected.onClick(item) }

                root.setOnLongClickListener {
                    onItemSelected.onLongClick(item)
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