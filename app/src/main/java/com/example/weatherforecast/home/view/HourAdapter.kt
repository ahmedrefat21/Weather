package com.example.weatherforecast.home.view


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.ItemHourlyBinding
import com.example.weatherforecast.helpers.checkLanguage
import com.example.weatherforecast.helpers.getHour
import com.example.weatherforecast.helpers.setIcon
import com.example.weatherforecast.model.Hourly
import java.text.NumberFormat


class HourAdapter (private var context: Context): ListAdapter<Hourly, HourViewHolder>(
    HourDiffUtil()
){
    private lateinit var binding : ItemHourlyBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyBinding.inflate(inflater,parent,false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val currentObj = getItem(position)
        var locale = checkLanguage(context)
        val numberFormat = NumberFormat.getInstance(locale)
        holder.binding.tvHourlyTemp.text = numberFormat.format(currentObj.temp.toInt()).toString()+" °"
        holder.binding.tvHourlyTime.text = getHour(context,currentObj.dt)
        setIcon(currentObj.weather[0].icon, binding.ivHourlyIcon)
//        Glide.with(context).load("https://openweathermap.org/img/wn/"+ currentObj.weather[0].icon+"@2x.png")
//            .into(holder.binding.ivHourlyIcon)
    }
}
class HourViewHolder (var binding: ItemHourlyBinding): RecyclerView.ViewHolder(binding.root)

class HourDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}