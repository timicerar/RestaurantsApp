package si.um.feri.restaurantsapp.retrofit.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import si.um.feri.restaurantsapp.retrofit.models.*

interface RestaurantsAPI {

    /**
    RESTAURANT DATA
     */
    @GET("restaurant/all")
    fun getAllRestaurantsAsync(): Deferred<Response<List<RestaurantJacksonModel>>>

    @GET("schedule/all")
    fun getAllSchedulesAsync(): Deferred<Response<List<ScheduleJacksonModel>>>

    @GET("day/all")
    fun getAllDaysAsync(): Deferred<Response<List<DayJacksonModel>>>

    /**
    RATINGS
     */
    @GET("rating/restaurant/{idRestaurant}")
    fun getRatingsByRestaurantIdAsync(@Path("idRestaurant") idRestaurant: Int): Deferred<Response<List<RatingJacksonModel>>>

    @GET("rating/user/googleId/{googleUserId}")
    fun getRatingsByUserGoogleIdAsync(@Path("googleUserId") googleUserId: String): Deferred<Response<List<RatingJacksonModel>>>

    @GET("rating/user/googleId/{googleUserId}/restaurant/{restaurantId}")
    fun getRatingsByUserGoogleIdAndRestaurantIdRestaurant(@Path("googleUserId") googleUserId: String, @Path("restaurantId") restaurantId: Int): Deferred<Response<List<RatingJacksonModel>>>

    @GET("rating/restaurant/{idRestaurant}/user/googleId/{userGoogleId}")
    fun checkIfUserAlreadyRatedTheRestaurantAsync(
        @Path("idRestaurant") idRestaurant: Int,
        @Path("userGoogleId") userGoogleId: String
    ): Deferred<Response<RatingJacksonModel>>

    @POST("rating/add")
    fun insertRatingAsync(@Body ratingJacksonModel: RatingJacksonModel): Deferred<Response<RatingJacksonModel>>

    /**
    COMMENTS
     */
    @GET("comment/restaurant/{idRestaurant}")
    fun getCommentsByRestaurantIdAsync(@Path("idRestaurant") idRestaurant: Int): Deferred<Response<List<CommentJacksonModel>>>

    @GET("comment/user/googleId/{googleUserId}")
    fun getCommentsByUserGoogleIdAsync(@Path("googleUserId") googleUserId: String): Deferred<Response<List<CommentJacksonModel>>>

    @POST("comment/add")
    fun insertCommentAsync(@Body commentJacksonModel: CommentJacksonModel): Deferred<Response<CommentJacksonModel>>

    @PUT("comment/update/{idComment}")
    fun updateCommentAsync(
        @Body commentJacksonModel: CommentJacksonModel,
        @Path("idComment") idComment: Int
    ): Deferred<Response<CommentJacksonModel>>

    @DELETE("comment/delete/{idComment}")
    fun deleteCommentAsync(@Path("idComment") idComment: Int?): Deferred<Response<Boolean>>

    /**
    USER
     */
    @GET("user/googleId/{googleUserId}")
    fun getUserByUserGoogleIdAsync(@Path("googleUserId") userGoogleId: String): Deferred<Response<UserJacksonModel>>

    @GET("user/email/{email}")
    fun getUserByEmailAsync(@Path("email") email: String): Deferred<Response<UserJacksonModel>>

    @GET("user/email/{email}/googleId/{googleUserId}")
    fun getUserByEmailOrGoogleUserId(@Path("email") email: String, @Path("googleUserId") userGoogleId: String): Deferred<Response<Boolean>>

    @POST("user/add")
    fun insertUserAsync(@Body userJacksonModel: UserJacksonModel): Deferred<Response<UserJacksonModel>>

    @PUT("user/update/{idUser}")
    fun updateUserAsync(@Body userJacksonModel: UserJacksonModel, @Path("idUser") idUser: Int): Deferred<Response<UserJacksonModel>>

}