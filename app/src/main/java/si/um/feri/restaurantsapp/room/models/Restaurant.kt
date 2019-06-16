package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Restaurant(
    @PrimaryKey
    val restaurantId: Int?,
    val name: String?,
    val description: String?,
    val photoUrl: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    var currentRating: Double?,
    val type: String?,
    var isFavorite: Boolean = false
) : Parcelable