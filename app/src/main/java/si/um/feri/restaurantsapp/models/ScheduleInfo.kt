package si.um.feri.restaurantsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleInfo (
    val name: String?,
    val fullSchedule: List<ScheduleWithDay>
) : Parcelable