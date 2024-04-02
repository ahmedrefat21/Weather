package com.example.weatherforecast.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritePlace")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val address: String?
): Parcelable {

    constructor(): this(1,0.0,0.0,"")
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteEntity> {
        override fun createFromParcel(parcel: Parcel): FavoriteEntity {
            return FavoriteEntity(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteEntity?> {
            return arrayOfNulls(size)
        }
    }
}

