package si.um.feri.restaurantsapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.activity_login.*
import si.um.feri.restaurantsapp.viewmodels.LoginViewModel

private const val ACTIVITY = "activity"
private const val MAIN_ACTIVITY = 1
private const val RC_SIGN_IN: Int = 1

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        if (loginViewModel.userLoggedIn()) {
            openMainScreen()
        }

        button_google_sign_in.setOnClickListener {
            val signInIntent = loginViewModel.signInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        loginViewModel.userLoggedInSuccessfully.observe(this, Observer { userLoggedIn ->
            if (userLoggedIn == null)
                return@Observer

            if (userLoggedIn)
                openMainScreen()
        })

        loginViewModel.exception.observe(this, Observer { exception ->
            if (exception == null)
                return@Observer

            handleExceptions(exception)
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == RC_SIGN_IN) {
            loginViewModel.loginWithGoogle(intent)
        }
    }

    private fun openMainScreen() {
        val intent = Intent(this, LoadingSpinnerActivity::class.java)
        intent.putExtra(ACTIVITY, MAIN_ACTIVITY)
        startActivity(intent)
        finish()
    }

    private fun handleExceptions(exception: Exception) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> toast(getString(R.string.register_weak_pw))
            is FirebaseAuthInvalidCredentialsException -> toast(getString(R.string.register_login_wrong_email_password))
            is FirebaseAuthUserCollisionException -> toast(getString(R.string.register_login_email_exists))
            is FirebaseAuthInvalidUserException -> toast(getString(R.string.login_user_not_found))
            is ApiException -> toast(getString(R.string.sign_in_failed))
            is FirebaseNetworkException -> toast(getString(R.string.check_internet_login))
        }
    }

    private fun toast(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show()
    }
}
