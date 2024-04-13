package com.example.weatherforecast.prefernces

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherforecast.Constants

class SharedPreference private constructor(context: Context) : SharedPreferenceInterface {

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
    }

   companion object {
       @Volatile
       private var instance : SharedPreference? = null

       fun getInstance(context: Context) : SharedPreference {
           return instance ?: synchronized(context){
               val instance = SharedPreference(context)
               this.instance = instance

               instance
           }

       }
   }
    override fun setTemperature(temperature: String) {
        editor!!.putString(Constants.TEMPERATURE, temperature)
        editor!!.commit()
    }
    override fun getTemperature() = sharedPreferences!!.getString(Constants.TEMPERATURE, "metric")!!

    override fun setWindSpeed(windSpeed: String){
        editor!!.putString(Constants.WIND_SPEED, windSpeed)
        editor!!.commit()
    }
    override fun getWindSpeed() = sharedPreferences!!.getString(Constants.WIND_SPEED, "metric")!!

    override fun setLanguage(language: String){
        editor!!.putString(Constants.LANGUAGE, language)
        editor!!.commit()
    }
    override fun getLanguage() = sharedPreferences!!.getString(Constants.LANGUAGE, "en")!!

    override fun setUnit(unit:String){
        editor!!.putString(Constants.UNIT, unit)
        editor!!.commit()
    }
    override fun getUnit() = sharedPreferences?.getString(Constants.UNIT,"metric").toString()

    override fun setNotification(notification: String){
        editor!!.putString(Constants.NOTIFICATION, notification)
        editor!!.commit()
    }
    override fun getNotification() = sharedPreferences!!.getString(Constants.NOTIFICATION, "")!!

    override fun setMap(map:String){
        editor!!.putString(Constants.MAP,map)
        editor!!.commit()
    }
    override fun getMap() = sharedPreferences!!.getString(Constants.MAP,"")

    override fun setLatAndLon(lat:Double, lon:Double){
        editor!!.putFloat(Constants.MAP_VALUE_LAT,lat.toFloat())
        editor!!.putFloat(Constants.MAP_VALUE_LON,lon.toFloat())
        editor!!.commit()
    }
    override fun setLatAndLonHome(lat:Double, lon:Double){
        editor!!.putFloat(Constants.LAT_HOME,lat.toFloat())
        editor!!.putFloat(Constants.LON_HOME,lon.toFloat())
        editor!!.commit()
    }

    override fun getLatHome():Double{
        return  sharedPreferences!!.getFloat(Constants.LAT_HOME,0.0f).toDouble()
    }

    override fun getLonHome():Double{
        return  sharedPreferences!!.getFloat(Constants.LON_HOME,0.0f).toDouble()
    }
    override fun getLat():Double{
        return sharedPreferences!!.getFloat(Constants.MAP_VALUE_LAT,0.0f).toDouble()
    }
    override fun getLon():Double{
        return sharedPreferences!!.getFloat(Constants.MAP_VALUE_LON,0.0f).toDouble()
    }

    fun setLocationWay(location: String){
        editor!!.putString(Constants.LOCATION, location)
        editor!!.commit()
    }

    fun getSavedLocationWay(): String {
        return sharedPreferences!!.getString(Constants.LOCATION, "")!!
    }

















}