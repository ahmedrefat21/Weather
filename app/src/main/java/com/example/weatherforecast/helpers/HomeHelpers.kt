package com.example.weatherforecast.helpers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.location.Address
import android.location.Geocoder
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.entity.AlertEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



fun checkLanguage(context: Context): Locale {
    var locale = if (SharedPreference.getInstance(context).getLanguage() == "ar"){
        Locale("ar")
    }else{
        Locale("en")
    }
    return locale
}

fun getHour(context: Context, hour: Long): String {
    var locale =checkLanguage(context)
    val time = Date(hour * 1000)
    val dateFormat = SimpleDateFormat("h:mm aaa",locale)
    return dateFormat.format(time)
}


fun getDate(context: Context, date: Long):String{
    var locale =checkLanguage(context)
    val time = Date(date * 1000)
    val dateFormat = SimpleDateFormat("EEEE, dd LLL", locale)
    return dateFormat.format(time)
}

fun getDays(context: Context, days: Long):String{
    var locale =checkLanguage(context)
    val time = Date(days * 1000)
    val dateFormat = SimpleDateFormat("EEEE", locale)
    return dateFormat.format(time)
}


fun getLocationName(context: Context, lat: Double?, lon: Double?):String{
    val locale =checkLanguage(context)
    val address:MutableList<Address>?
    val geocoder= Geocoder(context,locale)
    address =geocoder.getFromLocation(lat?:0.0,lon?:0.0,1)
    if (address?.isEmpty()==true) {
        return "UnKnown Place"
    } else if (address?.get(0)?.countryName.isNullOrEmpty()) {
        return "UnKnown Place"
    } else if (address?.get(0)?.adminArea.isNullOrEmpty()) {
        return address?.get(0)?.countryName.toString()
    } else{
        return address?.get(0)?.countryName.toString()+", "+address?.get(0)?.adminArea
    }
}

fun checkNetworkConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        networkInfo.isConnected
    }
}




private var isDialogShowing = false

fun createDialog(title:String="", message:String="", view: View?=null,
                 context: Context, sure:()->Unit, cancel:()->Unit){
    if (!isDialogShowing) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(view)
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                cancel()
                dialog.dismiss()

            }
            .setPositiveButton(context.getString(R.string.done)) { dialog, _ ->
                sure()
                dialog.dismiss()

            }
            .show()
    }
}

fun formatLong(dateTimeInMillis: Long, pattern: String): String {
    val resultFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val date = Date(dateTimeInMillis)
    return resultFormat.format(date)
}

fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}





