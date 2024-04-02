package com.example.weatherforecast.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Current
import java.util.*

@Entity(tableName = "Alert")
data class AlertEntity(@PrimaryKey var id: String = UUID.randomUUID().toString(),
                       var lat:Double, var lon:Double, var current: Current?,
                       var alert:List<Alert>, var notification:String,
                       var startDate: String?, var startTime: String?
) {

    constructor():this(UUID.randomUUID().toString(),0.0,0.0,null, emptyList(),
        "", null, null)


}