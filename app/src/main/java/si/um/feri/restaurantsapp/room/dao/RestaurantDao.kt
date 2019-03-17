package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import si.um.feri.restaurantsapp.room.models.Restaurant

@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(restaurant: Restaurant)

    @Query("SELECT 1 FROM Restaurant")
    fun isDataLoaded(): Boolean

    @Query("SELECT * FROM Restaurant ORDER BY name ASC")
    fun getAllRestaurantsAscByName(): List<Restaurant>

    @Query("SELECT * FROM Restaurant WHERE restaurantId = :restaurantId")
    fun getRestaurantByRestaurantId(restaurantId: Long): Restaurant
}