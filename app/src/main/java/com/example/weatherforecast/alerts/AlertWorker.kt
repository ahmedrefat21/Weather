package com.example.weatherforecast.alerts

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.Constants
import com.example.weatherforecast.helpers.checkNetworkConnection
import com.example.weatherforecast.helpers.createAlarmDialog
import com.example.weatherforecast.helpers.createNotification
import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.Repository
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.network.RemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AlertWorker (private var context: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(context,workerParameters){

    private lateinit var repo: Repository
    private lateinit var rssult:Result
    private var flag  = 0
    private var onlineAlertDescription = ""
    private var offlineAlertDescription =""


    override suspend fun doWork(): Result {
        repo = RepositoryImpl.getInstance(
            RemoteDataSourceImpl.getInstance(),
            LocalDataSourceImp(context)
        )

        val start = inputData.getLong(Constants.TIME, 0)
        val alertID = inputData.getString(Constants.ID)

        flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        delay(start)

        return try {
            val alert = repo.getAlertById(alertID.toString())
            if (checkNetworkConnection(context)) {
                getAlertMessageFromApi(alert, repo)
            } else {
                getAlertMessageOffline(alert)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }


    private fun isNotificationEnabled(): Boolean {
        return SharedPreference.getInstance(context).getNotification() == "enable"
    }
    private suspend fun getAlertMessageOffline(entity: AlertEntity){
        if (isNotificationEnabled()) {
            if (entity.alert.isEmpty()){
                offlineAlertDescription = "Weather Description now : "+entity.current!!.weather[0].description
            }else{
                offlineAlertDescription = entity.alert[0].description
            }
            sendAlert(entity,offlineAlertDescription)
        }

    }
    private suspend fun sendAlert(entity: AlertEntity, message: String){
        if (entity.notification == "notification") {
            createNotification(message,context)
            repo.deleteAlert(entity)
        } else {
            createAlarmDialog(context,message,entity,flag,repo)
        }
    }
    private suspend fun getAlertMessageFromApi(entity: AlertEntity, repository: Repository){
        val weatherResponse = repository.getWeatherAlert(
            entity.lat,
            entity.lon,
            SharedPreference.getInstance(context).getUnit(),
            SharedPreference.getInstance(context).getLanguage())

        withContext(Dispatchers.IO){
            if (!weatherResponse.equals(null)){
                if (!weatherResponse.alerts.isNullOrEmpty()) {
                    onlineAlertDescription =entity.alert[0].description
                }else{
                    onlineAlertDescription = "Weather Description now : "+entity.current!!.weather[0].description
                }
                rssult= Result.success()

            }else{
                onlineAlertDescription = "The Weather is Fine Today"
                rssult= Result.failure()
            }
        }

        if (isNotificationEnabled()) {
            sendAlert(entity,onlineAlertDescription)
        }

    }





}
