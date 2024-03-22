package com.example.weatherforecast.home.view

import Daily
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.ItemDailyBinding
import com.example.weatherforecast.helpers.getDay


class DayAdapter (private var context: Context): ListAdapter<Daily, WeakViewHolder>(DayDiffUtil()){
    private lateinit var binding : ItemDailyBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeakViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyBinding.inflate(inflater,parent,false)
        return WeakViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeakViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.tvDailyWeekDay.text = getDay(currentObj.dt)
        holder.binding.tvDailyDescription.text = currentObj.weather[0].description
        holder.binding.tvDailyMaxMinTemp.text = currentObj.temp.max.toInt().toString()+ "° / "+ currentObj.temp.min.toInt().toString()+"°"
        Glide.with(context).load("https://openweathermap.org/img/wn/"+ currentObj.weather[0].icon+"@2x.png")
            .into(holder.binding.ivDailyIcon)
    }

}
class WeakViewHolder (var binding: ItemDailyBinding): RecyclerView.ViewHolder(binding.root)

class DayDiffUtil: DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}