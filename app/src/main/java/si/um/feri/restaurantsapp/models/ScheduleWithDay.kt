package si.um.feri.restaurantsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import si.um.feri.restaurantsapp.room.models.Day
import java.time.LocalTime

@Parcelize
data class ScheduleWithDay (
    val scheduleId: Int?,
    val orderBy: Int?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val day: Day
) : Parcelable