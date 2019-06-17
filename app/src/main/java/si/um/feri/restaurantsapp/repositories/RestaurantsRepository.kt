package si.um.feri.restaurantsapp.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import si.um.feri.restaurantsapp.retrofit.interfaces.ClientAPI
import si.um.feri.restaurantsapp.retrofit.interfaces.RestaurantsAPI
import si.um.feri.restaurantsapp.retrofit.models.CommentJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.RatingJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.UserJacksonModel
import si.um.feri.restaurantsapp.room.dao.RestaurantsDatabase
import si.um.feri.restaurantsapp.room.models.Day
import si.um.feri.restaurantsapp.room.models.Favorite
import si.um.feri.restaurantsapp.room.models.Restaurant
import si.um.feri.restaurantsapp.room.models.Schedule
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val NO_INTERNET_CONNECTION = "No internet connection!"

class RestaurantsRepository(context: Context) {

    private val client = ClientAPI().getClient().create(RestaurantsAPI::class.java)
    private val restaurantsDatabase = RestaurantsDatabase.getDatabase(context)
    private val restaurantDao = restaurantsDatabase.restaurantDao()
    private val dayDao = restaurantsDatabase.dayDao()
    private val scheduleDao = restaurantsDatabase.scheduleDao()
    private val favoriteDao = restaurantsDatabase.favoriteDao()

    /**
     * INSERT RESTAURANT TO FAVORITES
     */
    suspend fun insertFavoriteRestaurant(favorite: Favorite?) {
        withContext(Dispatchers.IO) {
            favoriteDao.insertFavoriteRestaurant(favorite)
        }
    }

    /**
     * REMOVE RESTAURANT FROM FAVORITES
     */
    suspend fun deleteFavoriteRestaurant(favorite: Favorite) {
        withContext(Dispatchers.IO) {
            favoriteDao.deleteFavoriteRestaurant(favorite)
        }
    }

    /**
     * UPDATE RESTAURANT
     */
    suspend fun updateRestaurant(restaurant: Restaurant) {
        withContext(Dispatchers.IO) {
            restaurantDao.updateRestaurant(restaurant)
        }
    }

    /**
     * GET FAVORITE BY GOOGLE ID AND RESTAURANT ID
     */
    suspend fun getFavoriteByUserGoogleIdAndRestaurantId(userGoogleId: String, restaurantId: Int): Favorite {
        return withContext(Dispatchers.IO) {
            favoriteDao.getFavoriteByUserGoogleIdAndRestaurantId(userGoogleId, restaurantId)
        }
    }

    /**
     * GET FAVORITES BY USER GOOGLE ID
     */
    suspend fun getFavoritesByUserGoogleId(userGoogleId: String): List<Favorite> {
        return withContext(Dispatchers.IO) {
            favoriteDao.getFavoritesByUserGoogleId(userGoogleId)
        }
    }

    /**
     * GET ALL RESTAURANTS
     */
    suspend fun getRestaurantsData(): List<Restaurant> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isDataLoaded()) {
                    loadRestaurantDataFromWeb()
                }

                return@withContext restaurantDao.getAllRestaurantsAscByName()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET RESTAURANT BY ID
     */
    suspend fun getRestaurantByRestaurantId(restaurantId: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            restaurantDao.getRestaurantByRestaurantId(restaurantId)
        }
    }

    /**
     * GET SCHEDULES BY RESTAURANT ID
     */
    suspend fun getSchedulesByRestaurantId(restaurantId: Int): List<Schedule> {
        return withContext(Dispatchers.IO) {
            scheduleDao.getSchedulesByRestaurantId(restaurantId)
        }
    }

    /**
     * GET ALL DAYS
     */
    suspend fun getAllDays(): List<Day> {
        return withContext(Dispatchers.IO) {
            dayDao.getAllDays()
        }
    }

    /**
     * CHECK IF DATA IS LOADED IN ROOM
     */
    private suspend fun isDataLoaded(): Boolean {
        return withContext(Dispatchers.IO) {
            restaurantDao.isDataLoaded()
        }
    }

    /**
     * LOAD RESTAURANT DATA FROM WEB AND SAVE IT TO ROOM
     */
    private suspend fun loadRestaurantDataFromWeb() {
        return withContext(Dispatchers.IO) {
            try {
                val days =
                    client.getAllDaysAsync().await().body()
                val restaurants =
                    client.getAllRestaurantsAsync().await().body()
                val schedules =
                    client.getAllSchedulesAsync().await().body()

                for (day in days!!) {
                    insertDay(day = Day(day.idDay, day.name))
                }

                for (restaurant in restaurants!!) {
                    insertRestaurant(
                        restaurant = Restaurant(
                            restaurant.idRestaurant,
                            restaurant.name,
                            restaurant.description,
                            restaurant.photoUrl,
                            restaurant.address,
                            restaurant.latitude,
                            restaurant.longitude,
                            restaurant.currentRating,
                            restaurant.type
                        )
                    )
                }

                for (schedule in schedules!!) {
                    insertSchedule(
                        schedule = Schedule(
                            schedule.idSchedule,
                            schedule.orderBy,
                            schedule.startTime,
                            schedule.endTime,
                            schedule.restaurant?.idRestaurant,
                            schedule.day?.idDay
                        )
                    )
                }
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * INSERT RESTAURANT (ROOM)
     */
    private fun insertRestaurant(restaurant: Restaurant) {
        restaurantDao.insertRestaurant(restaurant)
    }

    /**
     * INSERT DAY (ROOM)
     */
    private fun insertDay(day: Day) {
        dayDao.insertDay(day)
    }

    /**
     * INSERT SCHEDULE (ROOM)
     */
    private fun insertSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }

    /**
     * GET USER BY GOOGLE ID
     */
    suspend fun getUserByUserGoogleId(userGoogleId: String): UserJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.getUserByUserGoogleIdAsync(userGoogleId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET USER BY EMAIL
     */
    suspend fun getUserByEmail(email: String): UserJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.getUserByEmailAsync(email).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * CHECK IF USER EXISTS
     */
    suspend fun getUserByEmailOrGoogleUserId(email: String, userGoogleId: String): Boolean? {
        return withContext(Dispatchers.IO) {
            try {
                client.getUserByEmailOrGoogleUserIdAsync(email, userGoogleId).await().body() ?: throw Exception()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * INSERT USER (RETROFIT)
     */
    suspend fun insertUser(userJacksonModel: UserJacksonModel): UserJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.insertUserAsync(userJacksonModel).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * UPDATE USER (RETROFIT)
     */
    suspend fun updateUser(userJacksonModel: UserJacksonModel, userId: Int): UserJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.updateUserAsync(userJacksonModel, userId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET ALL RATINGS BY RESTAURANT ID
     */
    suspend fun getRatingsByRestaurantId(restaurantId: Int): List<RatingJacksonModel>? {
        return withContext(Dispatchers.IO) {
            try {
                client.getRatingsByRestaurantIdAsync(restaurantId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET RATINGS BY GOOGLE USER ID
     */
    suspend fun getRatingsByUserGoogleId(googleUserId: String): List<RatingJacksonModel>? {
        return withContext(Dispatchers.IO) {
            try {
                client.getRatingsByUserGoogleIdAsync(googleUserId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET RATINGS BY GOOGLE USER ID AND RESTAURANT ID
     */
    suspend fun getRatingsByUserGoogleIdAndRestaurantId(
        googleUserId: String,
        restaurantId: Int
    ): List<RatingJacksonModel>? {
        return withContext(Dispatchers.IO) {
            try {
                client.getRatingsByUserGoogleIdAndRestaurantIdRestaurantAsync(googleUserId, restaurantId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }


    /**
     * CHECK IF USER HAS ALREADY RATED THE RESTAURANT
     */
    suspend fun checkIfUserAlreadyRatedTheRestaurant(restaurantId: Int, userGoogleId: String): RatingJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.checkIfUserAlreadyRatedTheRestaurantAsync(restaurantId, userGoogleId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * INSERT RATING (BY USER)
     */
    suspend fun insertRating(ratingJacksonModel: RatingJacksonModel): RatingJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.insertRatingAsync(ratingJacksonModel).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET COMMENTS BY RESTAURANT ID
     */
    suspend fun getCommentsByRestaurantId(restaurantId: Int): List<CommentJacksonModel>? {
        return withContext(Dispatchers.IO) {
            try {
                client.getCommentsByRestaurantIdAsync(restaurantId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * GET COMMENTS BY USER GOOGLE ID
     */
    suspend fun getCommentsByUserGoogleId(googleUserId: String): List<CommentJacksonModel>? {
        return withContext(Dispatchers.IO) {
            try {
                client.getCommentsByUserGoogleIdAsync(googleUserId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * INSERT COMMENT (RETROFIT)
     */
    suspend fun insertComment(commentJacksonModel: CommentJacksonModel): CommentJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.insertCommentAsync(commentJacksonModel).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * UPDATE COMMENT (RETROFIT)
     */
    suspend fun updateComment(commentJacksonModel: CommentJacksonModel, commentId: Int): CommentJacksonModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.updateCommentAsync(commentJacksonModel, commentId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }

    /**
     * DELETE COMMENT (RETROFIT)
     */
    suspend fun deleteComment(commentId: Int?): Boolean? {
        return withContext(Dispatchers.IO) {
            try {
                client.deleteCommentAsync(commentId).await().body()
            } catch (e: ConnectException) {
                throw ConnectException(NO_INTERNET_CONNECTION)
            } catch (e: UnknownHostException) {
                throw UnknownHostException(NO_INTERNET_CONNECTION)
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException(NO_INTERNET_CONNECTION)
            }
        }
    }
}