package si.um.feri.restaurantsapp.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_restaurant.*
import si.um.feri.restaurantsapp.MapsActivity
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.adapters.RestaurantDetailAdapter
import si.um.feri.restaurantsapp.room.models.Favorite
import si.um.feri.restaurantsapp.room.models.Restaurant
import si.um.feri.restaurantsapp.viewmodels.LoginViewModel
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

private const val NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION"

class RestaurantFragment : Fragment() {

    companion object {
        fun newInstance(): RestaurantFragment = RestaurantFragment()
    }

    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.restaurantDetailsRecycleView)
        val adapter = RestaurantDetailAdapter(
            requireContext(),
            ::favoriteRestaurant,
            ::removeFavoriteRestaurant
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        val bundle = arguments
        val restaurantId = bundle?.getInt("restaurantId")

        if (restaurantId != null) {
            restaurantViewModel.getRestaurantDetailList(restaurantId)
            restaurantViewModel.restaurantDetailList.observe(this, Observer { restaurantDetailList ->
                if (restaurantDetailList == null)
                    return@Observer

                restaurantMapButton.setOnClickListener {
                    for (details in restaurantDetailList) {
                        if (details is Restaurant) {
                            val intent = Intent(requireActivity(), MapsActivity::class.java)
                            intent.putExtra("restaurant", details)
                            requireActivity().startActivity(intent)
                            break
                        }
                    }
                }

                restaurantDetailList.let {
                    adapter.setRestaurantDetails(restaurantDetailList)
                }
            })
        }

        restaurantViewModel.noInternet.observe(this, Observer { noInternet ->
            if (noInternet == null)
                return@Observer

            if (noInternet) {
                openFragment(NoInternetFragment.newInstance(), NO_INTERNET_CONNECTION)
            }
        })
    }

    override fun onResume() {
        val bundle = arguments
        val restaurantId = bundle?.getInt("restaurantId")

        if (restaurantId != null)
            restaurantViewModel.getRestaurantDetailList(restaurantId)

        super.onResume()
    }

    private fun favoriteRestaurant(restaurant: Restaurant) {
        loginViewModel.getCurrentUser()
        loginViewModel.currentUser.observe(this, Observer { currentUser ->
            if (currentUser == null)
                return@Observer

            val favorite = restaurant.restaurantId?.let { restaurantId ->
                Favorite(0, currentUser.uid, restaurantId)
            }

            restaurantViewModel.addRestaurantToFavorites(favorite!!)
        })

    }

    private fun removeFavoriteRestaurant(restaurant: Restaurant) {
        loginViewModel.getCurrentUser()
        loginViewModel.currentUser.observe(this, Observer { currentUser ->
            if (currentUser == null)
                return@Observer

            restaurant.restaurantId?.let {
                restaurantViewModel.removeRestaurantFromFavorites(it, currentUser.uid)
            }
        })
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
}