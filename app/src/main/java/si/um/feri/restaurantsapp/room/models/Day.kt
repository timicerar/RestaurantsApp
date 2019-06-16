package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Day(
    @PrimaryKey
    val dayId: Int? = 0,
    val name: String?
) : Parcelable