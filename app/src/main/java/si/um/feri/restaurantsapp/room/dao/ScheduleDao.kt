package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import si.um.feri.restaurantsapp.room.models.Day
import si.um.feri.restaurantsapp.room.models.Schedule

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule WHERE fkRestaurantId = :fkRestaurantId")
    fun getSchedulesByRestaurantId(fkRestaurantId: Long): List<Schedule>
}