package com.dvt.weatherforecast.ui.home.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.data.models.Daily
import com.dvt.weatherforecast.databinding.LayoutForecastItemBinding
import com.dvt.weatherforecast.utils.convertTimeStamp

class ForeCastAdapter : ListAdapter<Daily, ForeCastAdapter.ForeCastViewHolder>(diffUtil) {

    inner class ForeCastViewHolder(private val binding: LayoutForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Daily) {
            with(binding) {
                tvDateName.text = convertTimeStamp(item.dt.toLong())
                tvWeatherValue.text = item.temp.day.toInt().toString() + " \u2103"


                val condition = item.weather[0]

                val id = condition.id.toString()

                val context = binding.root.context

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
                    condition.id > 800 -> {
                        updateBackgrounds(R.drawable.partlysunny)

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

val diffUtil = object : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }


}