package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Day(
    @PrimaryKey
    val dayId: Int? = 0,
    val name: String?
)