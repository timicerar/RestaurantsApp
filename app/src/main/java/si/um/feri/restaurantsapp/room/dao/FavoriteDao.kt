package si.um.feri.restaurantsapp.room.dao

import android.arch.persistence.room.*
import si.um.feri.restaurantsapp.room.models.Favorite

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteRestaurant(favorite: Favorite)

    @Delete
    fun deleteFavoriteRestaurant(favorite: Favorite)

    @Query("SELECT * FROM Favorite WHERE userGoogleId = :userGoogleId AND restaurantId = :restaurantId")
    fun getFavoriteByUserGoogleIdAndRestaurantId(userGoogleId: String, restaurantId: Long): Favorite

    @Query("SELECT * FROM Favorite WHERE userGoogleId = :userGoogleId")
    fun getFavoritesByUserGoogleId(userGoogleId: String): List<Favorite>
}