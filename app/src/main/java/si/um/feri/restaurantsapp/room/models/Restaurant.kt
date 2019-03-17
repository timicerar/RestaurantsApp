package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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
    val currentRating: Double?,
    val type: String?,
    var isFavorite: Boolean = false
)