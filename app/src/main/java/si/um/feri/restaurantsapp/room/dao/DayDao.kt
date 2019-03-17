package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import si.um.feri.restaurantsapp.room.models.Day

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDay(day: Day)

    @Query("SELECT * FROM Day")
    fun getAllDays(): List<Day>
}