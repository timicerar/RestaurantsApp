package si.um.feri.restaurantsapp.room.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.time.LocalTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = Restaurant::class,
        parentColumns = arrayOf("restaurantId"),
        childColumns = arrayOf("fkRestaurantId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Day::class,
        parentColumns = arrayOf("dayId"),
        childColumns = arrayOf("fkDayId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Schedule(
    @PrimaryKey
    val scheduleId: Int?,
    val orderBy: Int?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    @ColumnInfo(index = true)
    val fkRestaurantId: Int?,
    @ColumnInfo(index = true)
    val fkDayId: Int?
)
