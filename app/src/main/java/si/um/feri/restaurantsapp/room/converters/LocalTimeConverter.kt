package si.um.feri.restaurantsapp.room.converters

import android.arch.persistence.room.TypeConverter
import android.os.Build
import android.support.annotation.RequiresApi
import java.time.LocalTime

class LocalTimeConverter {

    @TypeConverter
    fun fromStringToLocalTime(value: String?): LocalTime? {
        return LocalTime.parse(value)
    }

    @TypeConverter
    fun fromLocalTimeToString(localTime: LocalTime?): String? {
        return localTime.toString()
    }
}