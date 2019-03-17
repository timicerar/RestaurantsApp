package si.um.feri.restaurantsapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import si.um.feri.restaurantsapp.fragments.*
import si.um.feri.restaurantsapp.viewmodels.LoginViewModel
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

private const val ACTIVITY = "activity"
private const val LOGIN_ACTIVITY = -1
private const val HOME_FRAGMENT = "HOME_FRAGMENT"
private const val COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT"
private const val FAVORITES_FRAGMENT = "FAVORITES_FRAGMENT"
private const val RATINGS_FRAGMENT = "RATINGS_FRAGMENT"
private const val USER_PROFILE = "USER_PROFILE"

class MainActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)

        if (!loginViewModel.userLoggedIn())
            openLoginScreen()

        loginViewModel.getCurrentUser()
        loginViewModel.currentUser.observe(this, Observer { currentUser ->
            if (currentUser == null)
                return@Observer

            restaurantViewModel.insertUser(currentUser)

            nav_view.getHeaderView(0).nav_ime_in_priimek.text = currentUser.displayName
            nav_view.getHeaderView(0).nav_email.text = currentUser.email
            Picasso
                .with(this)
                .load(currentUser.photoUrl)
                .error(R.drawable.ic_app)
                .into(nav_view.getHeaderView(0).nav_user_profile_image)
        })

        nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            openFragmentOnMenuItemClicked(menuItem)
            drawer_layout.closeDrawers()
            true
        }

        setSupportActionBar(main_toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        nav_view.menu.getItem(0).isChecked = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, HomeFragment.newInstance(), HOME_FRAGMENT)
            .commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun openFragmentOnMenuItemClicked(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> openFragment(HomeFragment.newInstance(), HOME_FRAGMENT)
            R.id.nav_favorites -> openFragment(FavoritesFragment.newInstance(), FAVORITES_FRAGMENT)
            R.id.nav_comments -> openFragment(CommentsFragment.newInstance(), COMMENTS_FRAGMENT)
            R.id.nav_ratings -> openFragment(RatingsFragment.newInstance(), RATINGS_FRAGMENT)
            R.id.nav_profile -> openFragment(UserProfileFragment.newInstance(), USER_PROFILE)
            R.id.nav_logout -> {
                loginViewModel.signOut()
                openLoginScreen()
            }
        }
    }

    private fun openFragment(fragment: Fragment, tag: String, canGoBack: Boolean = true) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment, tag)
            .apply { if (canGoBack) addToBackStack(null) }
            .commit()
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoadingSpinnerActivity::class.java)
        intent.putExtra(ACTIVITY, LOGIN_ACTIVITY)
        startActivity(intent)
        finish()
    }
}
