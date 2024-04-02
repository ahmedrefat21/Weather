package com.example.weatherforecast.alerts.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ItemAlertBinding
import com.example.weatherforecast.helpers.getLocationName
import com.example.weatherforecast.model.entity.AlertEntity
import java.util.*

class AlertAdapter(private val context: Context ,
                   private val alertsOnClickListener: AlertsOnClickListener
) : ListAdapter<AlertEntity, AlertAdapter.AlertViewHolder>(AlertDiffUtils()){
    private lateinit var binding: ItemAlertBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentObj = getItem(position)
        binding.tvCityName.text = getLocationName(context,currentObj.lat,currentObj.lon)
        binding.tvdate.text = currentObj.startDate
        binding.tvTime.text = currentObj.startTime


        binding.ivDelete.setOnClickListener {
            alertsOnClickListener.deleteAlertItem(getItem(position))
        }
    }
    inner class AlertViewHolder(var binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)


}

class AlertDiffUtils : DiffUtil.ItemCallback<AlertEntity>(){

    override fun areItemsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem == newItem
    }

}
