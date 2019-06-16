package si.um.feri.restaurantsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (
    val name: String?,
    val address: String?
) : Parcelable