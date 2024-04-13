package com.example.weatherforecast.home.view


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ItemDailyBinding
import com.example.weatherforecast.helpers.checkLanguage
import com.example.weatherforecast.helpers.getDays
import com.example.weatherforecast.helpers.setIcon
import com.example.weatherforecast.model.Daily
import java.text.NumberFormat


class DayAdapter (private var context: Context): ListAdapter<Daily, DayViewHolder>(DayDiffUtil()){
    private lateinit var binding : ItemDailyBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyBinding.inflate(inflater,parent,false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val currentObj = getItem(position)
        var locale = checkLanguage(context)
        val numberFormat = NumberFormat.getInstance(locale)

        holder.binding.tvDailyDescription.text = currentObj.weather[0].description
        holder.binding.tvDailyMaxMinTemp.text = numberFormat.format(currentObj.temp.max.toInt()).toString()+ "° / "+ numberFormat.format(currentObj.temp.min.toInt()).toString()+"°"
        setIcon(currentObj.weather[0].icon, binding.ivDailyIcon)
//        Glide.with(context).load("https://openweathermap.org/img/wn/"+ currentObj.weather[0].icon+"@2x.png")
//            .into(holder.binding.ivDailyIcon)
        when (position) {
            0 -> {
                holder.binding.tvDailyWeekDay.text = context.getString(R.string.tomorrow)
                }
            else -> holder.binding.tvDailyWeekDay.text = getDays(context,currentObj.dt)
        }
    }

}
class DayViewHolder (var binding: ItemDailyBinding): RecyclerView.ViewHolder(binding.root)

class DayDiffUtil: DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}