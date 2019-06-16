package si.um.feri.restaurantsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Description(
    val name: String = "Opis restavracije",
    val description: String?
) : Parcelable