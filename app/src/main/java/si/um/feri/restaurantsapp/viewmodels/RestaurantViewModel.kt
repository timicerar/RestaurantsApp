package si.um.feri.restaurantsapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import si.um.feri.restaurantsapp.repositories.RestaurantsRepository
import si.um.feri.restaurantsapp.retrofit.models.UserJacksonModel
import si.um.feri.restaurantsapp.room.models.Favorite
import si.um.feri.restaurantsapp.room.models.Restaurant
import java.net.UnknownHostException
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

    private val _noInternet = MutableLiveData<Boolean>()
    val noInternet: LiveData<Boolean> = _noInternet

    private val _restaurantsList = MutableLiveData<List<Restaurant>>()
    val restaurantsList: LiveData<List<Restaurant>> = _restaurantsList

    /**
     * GET RESTAURANTS DATA
     */
    fun getRestaurantsData(userGoogleId: String) = launch {
        try {
            _loadingData.value = true
            _noInternet.value = false

            val restaurants = restaurantsRepository.getRestaurantsData()
            val userFavorites = restaurantsRepository.getFavoritesByUserGoogleId(userGoogleId)

            if (userFavorites.isNotEmpty()) {
                for (restaurant in restaurants) {
                    for (favorite in userFavorites) {
                        if (restaurant.restaurantId == favorite.restaurantId) {
                            restaurant.isFavorite = true
                        }
                    }
                }
            }

            _restaurantsList.value = restaurants

            _loadingData.value = false
        } catch (e: UnknownHostException) {
            _noInternet.value = true
        }
    }

    fun insertUser(currentUser: FirebaseUser) = launch {
        val isUserAdded = restaurantsRepository.getUserByEmailOrGoogleUserId(currentUser.email!!, currentUser.uid)

        if (!isUserAdded) {
            try {
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
            } catch (e: UnknownHostException) {
                _noInternet.value = true
            }
        }
    }

    fun addRestaurantToFavorites(favorite: Favorite) = launch {
        restaurantsRepository.insertFavoriteRestaurant(favorite)
    }

    fun removeRestaurantFromFavorites(restaurantId: Long, googleUserId: String) = launch {
        val favorite = restaurantsRepository.getFavoriteByUserGoogleIdAndRestaurantId(googleUserId, restaurantId)
        restaurantsRepository.deleteFavoriteRestaurant(favorite)
    }

}