package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val favoriteId: Int = 0,
    val userGoogleId: String,
    val restaurantId: Int
) : Parcelable
