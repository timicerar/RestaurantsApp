package si.um.feri.restaurantsapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import org.apache.commons.lang.StringEscapeUtils
import si.um.feri.restaurantsapp.models.*
import si.um.feri.restaurantsapp.repositories.RestaurantsRepository
import si.um.feri.restaurantsapp.retrofit.models.CommentJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.RatingJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.RestaurantJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.UserJacksonModel
import si.um.feri.restaurantsapp.room.models.Favorite
import si.um.feri.restaurantsapp.room.models.Restaurant
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import kotlin.coroutines.CoroutineContext

class RestaurantViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val parentJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    private val restaurantsRepository = RestaurantsRepository(application)

    private val _loadingData = MutableLiveData<Boolean>()
    val loadingData: LiveData<Boolean> = _loadingData

    private val _restaurantDetails = MutableLiveData<Restaurant>()
    val restaurantDetails: LiveData<Restaurant> = _restaurantDetails

    private val _noInternet = MutableLiveData<Boolean>()
    val noInternet: LiveData<Boolean> = _noInternet

    private val _restaurantsList = MutableLiveData<List<Restaurant>>()
    val restaurantsList: LiveData<List<Restaurant>> = _restaurantsList

    private val _restaurantDetailList = MutableLiveData<List<Any>>()
    val restaurantDetailList: LiveData<List<Any>> = _restaurantDetailList

    private val _usersOpinions = MutableLiveData<List<CommentWithRating>>()
    val usersOpinions: LiveData<List<CommentWithRating>> = _usersOpinions

    private val _firstComment = MutableLiveData<Boolean>()
    val firstComment: LiveData<Boolean> = _firstComment

    /**
     * GET RESTAURANTS DATA
     */
    fun getRestaurantsData() = launch {
        try {
            _loadingData.value = true
            _noInternet.value = false

            val restaurants = restaurantsRepository.getRestaurantsData()

            _restaurantsList.value = restaurants

            _loadingData.value = false
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        } catch (e: ConnectException) {
            _noInternet.value = true
        } catch (e: SocketTimeoutException) {
            _noInternet.value = true
        }
    }

    fun getRestaurantDetails(restaurantId: Int) = launch {
        _loadingData.value = true
        val restaurant = restaurantsRepository.getRestaurantByRestaurantId(restaurantId)
        _restaurantDetails.value = restaurant
        _loadingData.value = false
    }

    fun getRestaurantDetailList(restaurantId: Int) = launch {
        try {
            _loadingData.value = true
            _noInternet.value = false

            val restaurant = restaurantsRepository.getRestaurantByRestaurantId(restaurantId)
            val restaurantSchedule = restaurantsRepository.getSchedulesByRestaurantId(restaurantId)
            val days = restaurantsRepository.getAllDays()

            val location = Location("Lokacija", restaurant.address)
            val description = Description("Opis restavracije", restaurant.description)

            val schedulesWithDay = arrayListOf<ScheduleWithDay>()

            for (schedule in restaurantSchedule) {
                for (day in days) {
                    if (day.dayId == schedule.fkDayId) {
                        schedulesWithDay.add(
                            ScheduleWithDay(
                                schedule.scheduleId,
                                schedule.orderBy,
                                schedule.startTime,
                                schedule.endTime,
                                day
                            )
                        )
                        break
                    }
                }
            }

            val scheduleInfo = ScheduleInfo("Delovnik", schedulesWithDay)
            val commentInfo = CommentInfo("Komentarji in ocene restavracije")
            val commentWithRatingList = arrayListOf<CommentWithRating>()

            val comments = restaurantsRepository.getCommentsByRestaurantId(restaurantId)
            val ratings = restaurantsRepository.getRatingsByRestaurantId(restaurantId)

            if (comments != null && ratings != null) {
                for (comment in comments) {
                    for (rating in ratings) {
                        if ((comment.user!!.idUser == rating.user!!.idUser) && (comment.timeOfPublication == rating.timeOfPublication)) {
                            commentWithRatingList.add(CommentWithRating(comment, rating))
                            break
                        }
                    }
                }
            }

            val restaurantDetailList = mutableListOf<Any>()
            restaurantDetailList.add(restaurant)
            restaurantDetailList.add(location)
            restaurantDetailList.add(description)
            restaurantDetailList.add(scheduleInfo)

            if (commentWithRatingList.isNotEmpty()) {
                restaurantDetailList.add(commentInfo)

                commentWithRatingList.sortByDescending {
                    it.comment.timeOfPublication
                }

                restaurantDetailList.addAll(commentWithRatingList)
            }

            _restaurantDetailList.value = restaurantDetailList

            _loadingData.value = false
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        } catch (e: ConnectException) {
            _noInternet.value = true
        } catch (e: SocketTimeoutException) {
            _noInternet.value = true
        }
    }

    fun insertUser(currentUser: FirebaseUser) = launch {
        try {
            val isUserAdded = restaurantsRepository.getUserByEmailOrGoogleUserId(currentUser.email!!, currentUser.uid)

            if (!isUserAdded!!) {
                _noInternet.value = false
                restaurantsRepository.insertUser(
                    userJacksonModel = UserJacksonModel(
                        null,
                        currentUser.uid,
                        currentUser.displayName,
                        null,
                        null,
                        currentUser.email,
                        currentUser.photoUrl.toString()
                    )
                )
            }
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        } catch (e: ConnectException) {
            _noInternet.value = true
        } catch (e: SocketTimeoutException) {
            _noInternet.value = true
        }
    }

    fun addRestaurantToFavorites(favorite: Favorite) = launch {
        val restaurant = restaurantsRepository.getRestaurantByRestaurantId(favorite.restaurantId)
        restaurant.isFavorite = true
        restaurantsRepository.updateRestaurant(restaurant)
        restaurantsRepository.insertFavoriteRestaurant(favorite)
    }

    fun removeRestaurantFromFavorites(restaurantId: Int, googleUserId: String) = launch {
        val favorite = restaurantsRepository.getFavoriteByUserGoogleIdAndRestaurantId(googleUserId, restaurantId)
        val restaurant = restaurantsRepository.getRestaurantByRestaurantId(favorite.restaurantId)
        restaurant.isFavorite = false
        restaurantsRepository.updateRestaurant(restaurant)
        restaurantsRepository.deleteFavoriteRestaurant(favorite)
    }

    fun addRestaurantOpinion(commentText: String, rating: Float, userEmail: String, restaurantId: Int): Job {
        _loadingData.value = true
        _firstComment.value = false

        return launch {
            try {
                _loadingData.value = true
                _noInternet.value = false

                val encodedText = StringEscapeUtils.escapeJava(commentText)

                val currentUser = restaurantsRepository.getUserByEmail(userEmail)
                val restaurant = restaurantsRepository.getRestaurantByRestaurantId(restaurantId)
                val usersRatings = restaurantsRepository.getRatingsByUserGoogleIdAndRestaurantId(
                    currentUser?.googleUserId!!,
                    restaurant.restaurantId!!
                )
                val dateTime = LocalDateTime.now()

                if (usersRatings.isNullOrEmpty()) {
                    _firstComment.value = false
                }

                restaurantsRepository.insertComment(
                    CommentJacksonModel(
                        null,
                        encodedText,
                        false,
                        dateTime,
                        restaurant = RestaurantJacksonModel(
                            restaurant.restaurantId,
                            restaurant.name,
                            restaurant.description,
                            restaurant.photoUrl,
                            restaurant.address,
                            restaurant.latitude,
                            restaurant.longitude,
                            restaurant.currentRating,
                            restaurant.type
                        ),
                        user = currentUser
                    )
                )

                restaurantsRepository.insertRating(
                    RatingJacksonModel(
                        null,
                        rating.toDouble(),
                        dateTime,
                        restaurant = RestaurantJacksonModel(
                            restaurant.restaurantId,
                            restaurant.name,
                            restaurant.description,
                            restaurant.photoUrl,
                            restaurant.address,
                            restaurant.latitude,
                            restaurant.longitude,
                            restaurant.currentRating,
                            restaurant.type
                        ),
                        user = currentUser
                    )
                )

                if (usersRatings != null) {
                    if (usersRatings.isEmpty()) {
                        _firstComment.value = false
                        val newRatingValue = (restaurant.currentRating!! + rating) / 2

                        if (newRatingValue > 5.0)
                            restaurant.currentRating = 5.0
                        else
                            restaurant.currentRating = newRatingValue

                        restaurantsRepository.updateRestaurant(restaurant)
                    } else
                        _firstComment.value = true
                }

                _loadingData.value = false
            } catch (e: UnknownHostException) {
                _noInternet.value = true
            } catch (e: ConnectException) {
                _noInternet.value = true
            } catch (e: SocketTimeoutException) {
                _noInternet.value = true
            }
        }
    }

    fun getUsersOpinions(userGoogleId: String) = launch {
        try {
            _loadingData.value = true
            _noInternet.value = false

            val usersRatings = restaurantsRepository.getRatingsByUserGoogleId(userGoogleId)
            val usersComments = restaurantsRepository.getCommentsByUserGoogleId(userGoogleId)

            if (usersComments != null && usersRatings != null) {
                val usersCommentsWithRatings = arrayListOf<CommentWithRating>()

                for (comment in usersComments) {
                    for (rating in usersRatings) {
                        if ((comment.user!!.idUser == rating.user!!.idUser) && (comment.timeOfPublication == rating.timeOfPublication)) {
                            usersCommentsWithRatings.add(CommentWithRating(comment, rating))
                            break
                        }
                    }
                }

                _usersOpinions.value = usersCommentsWithRatings
            }

            _loadingData.value = false
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        } catch (e: ConnectException) {
            _noInternet.value = true
        } catch (e: SocketTimeoutException) {
            _noInternet.value = true
        }
    }

    fun removeOpinion(commentId: Int?) = launch {
        try {
            _loadingData.value = true
            _noInternet.value = false

            restaurantsRepository.deleteComment(commentId)

            _loadingData.value = false
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        } catch (e: ConnectException) {
            _noInternet.value = true
        } catch (e: SocketTimeoutException) {
            _noInternet.value = true
        }
    }

}