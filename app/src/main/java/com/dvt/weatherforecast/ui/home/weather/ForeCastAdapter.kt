package com.dvt.weatherforecast.ui.home.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.data.models.db.ForeCastEntity
import com.dvt.weatherforecast.databinding.LayoutForecastItemBinding
import com.dvt.weatherforecast.utils.convertTimeStamp

class ForeCastAdapter : ListAdapter<ForeCastEntity, ForeCastAdapter.ForeCastViewHolder>(diffUtil) {

    inner class ForeCastViewHolder(private val binding: LayoutForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForeCastEntity) {
            with(binding) {
                tvDateName.text = convertTimeStamp(item.lastUpdated)
                tvWeatherValue.text = item.normalTemp.toString() + " \u2103"

                val id = item.weatherCondition


                when {

                    //Thunderstorm
                    id.startsWith("2", true) -> {
                        updateBackgrounds(R.drawable.rain)
                    }

                    //drizzle
                    id.startsWith("3", true) -> {
                        updateBackgrounds(R.drawable.rain)
                    }

                    // Rain
                    id.startsWith("5", true) -> {
                        updateBackgrounds(R.drawable.rain)
                    }

                    //Snow
                    id.startsWith("6", true) -> {
                        updateBackgrounds(R.drawable.rain)

                    }

                    //Atmosphere
                    id.startsWith("7", true) -> {
                        updateBackgrounds(R.drawable.partlysunny)

                    }

                    //sunny/clear

                    id.equals("800", true) -> {
                        updateBackgrounds(R.drawable.clear)
                    }

                    // cloudy
                    id.toInt() > 800 -> {
                        updateBackgrounds(R.drawable.forest_cloudy)

                    }


                }

            }
        }

        private fun updateBackgrounds(cloudyColor: Int) {
            binding.imgWeatherType.setImageResource(cloudyColor)
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForeCastAdapter.ForeCastViewHolder {
        return ForeCastViewHolder(
            LayoutForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ForeCastAdapter.ForeCastViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
    }

}

val diffUtil = object : DiffUtil.ItemCallback<ForeCastEntity>() {
    override fun areItemsTheSame(oldItem: ForeCastEntity, newItem: ForeCastEntity): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: ForeCastEntity, newItem: ForeCastEntity): Boolean {
        return oldItem == newItem
    }


}