package com.example.focusclock

import android.os.Parcel
import android.os.Parcelable

data class TimeEntryHomeDisplay (
    val firebaseUUID: String,
    val startTime : String,
    val endTime : String,
    val selectedTask: String,
    val entryProject : String,
    val timeEntryPicRef: String,

    // NEED TO ADD
    val dateentry: String,
    // extra for display
    var durationTask: Double?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firebaseUUID)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(selectedTask)
        parcel.writeString(entryProject)
        parcel.writeString(timeEntryPicRef)
        parcel.writeString(dateentry)
        parcel.writeDouble(durationTask ?: 0.0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeEntryHomeDisplay> {
        override fun createFromParcel(parcel: Parcel): TimeEntryHomeDisplay {
            return TimeEntryHomeDisplay(parcel)
        }

        override fun newArray(size: Int): Array<TimeEntryHomeDisplay?> {
            return arrayOfNulls(size)
        }
    }
}