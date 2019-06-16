package si.um.feri.restaurantsapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.repositories.RestaurantsRepository
import si.um.feri.restaurantsapp.retrofit.models.UserJacksonModel
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

class LoginViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val parentJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    private val restaurantsRepository = RestaurantsRepository(application)

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(application.getString(R.string.google_client_id))
        .requestEmail()
        .build()
    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(application, googleSignInOptions)

    private val _exception = MutableLiveData<Exception>()
    val exception: LiveData<Exception> = _exception

    private val _noInternet = MutableLiveData<Boolean>()
    val noInternet: LiveData<Boolean> = _noInternet

    private val _userLoggedInSuccessfully = MutableLiveData<Boolean>()
    val userLoggedInSuccessfully: LiveData<Boolean> = _userLoggedInSuccessfully

    private val _currentUser = MutableLiveData<FirebaseUser>()
    val currentUser: LiveData<FirebaseUser> = _currentUser

    fun signInIntent() = googleSignInClient.signInIntent

    fun signOut() = firebaseAuth.signOut()

    fun userLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUser() = launch {
        _currentUser.value = firebaseAuth.currentUser
    }

    fun loginWithGoogle(intent: Intent?) = launch {
        try {
            _userLoggedInSuccessfully.value = false

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.await()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            if (result.user != null) {
                _userLoggedInSuccessfully.value = true
            }
        } catch (e: Exception) {
            println(e.message)
            _exception.value = e
        }
    }
}