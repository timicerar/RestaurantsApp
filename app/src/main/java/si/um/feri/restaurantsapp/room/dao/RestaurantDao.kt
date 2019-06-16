package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.*
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
    fun getRestaurantByRestaurantId(restaurantId: Int): Restaurant

    @Update
    fun updateRestaurant(restaurant: Restaurant)
}