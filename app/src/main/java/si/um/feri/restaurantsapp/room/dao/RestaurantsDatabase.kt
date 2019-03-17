package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import si.um.feri.restaurantsapp.room.converters.LocalTimeConverter
import si.um.feri.restaurantsapp.room.models.Day
import si.um.feri.restaurantsapp.room.models.Favorite
import si.um.feri.restaurantsapp.room.models.Restaurant
import si.um.feri.restaurantsapp.room.models.Schedule

@Database(
    entities = [Restaurant::class, Schedule::class, Day::class, Favorite::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalTimeConverter::class)
abstract class RestaurantsDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun dayDao(): DayDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantsDatabase? = null

        fun getDatabase(context: Context): RestaurantsDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantsDatabase::class.java,
                    "resturants_app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}