package com.example.weatherforecast.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getHourTime(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("k a", Locale.US)
    return dateFormat.format(data)
}

fun getDay(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("E, d/M", Locale.US)
    return dateFormat.format(data)
}

fun getCurrentTime(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("EEE , d/M/yyyy", Locale.US)
    return dateFormat.format(data)
}


fun getAddressEnglish(context: Context, lat: Double?, lon: Double?):String{
    var address:MutableList<Address>?
    val geocoder= Geocoder(context)
    address =geocoder.getFromLocation(lat?:0.0,lon?:0.0,1)
    if (address?.isEmpty()==true) {
        return "Unkown location"
    } else if (address?.get(0)?.countryName.isNullOrEmpty()) {
        return "Unkown Country"
    } else if (address?.get(0)?.adminArea.isNullOrEmpty()) {
        return address?.get(0)?.countryName.toString()
    } else{
        return address?.get(0)?.countryName.toString()+", "+address?.get(0)?.adminArea+", "+address?.get(0)?.locality
    }
}