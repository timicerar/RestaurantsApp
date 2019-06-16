package si.um.feri.restaurantsapp.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.adapters.RestaurantsAdapter
import si.um.feri.restaurantsapp.room.models.Restaurant
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

private const val NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION"

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.restaurant_recycle_view)
        val adapter = RestaurantsAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        restaurantViewModel.getRestaurantsData()
        restaurantViewModel.restaurantsList.observe(requireActivity(), Observer { restaurantList ->
            if (restaurantList == null)
                return@Observer

            restaurantList.let {
                adapter.setRestaurants(restaurantList)
            }
        })

        restaurantViewModel.loadingData.observe(requireActivity(), Observer {
            if (it!!) {
                loadginRestauransProgressBar.visibility = View.VISIBLE
            } else {
                loadginRestauransProgressBar.visibility = View.INVISIBLE
            }
        })

        editTextSearchRestaurants.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                restaurantViewModel.restaurantsList.observe(requireActivity(), Observer { restaurantList ->
                    if (restaurantList == null)
                        return@Observer

                    restaurantList.let {
                        val filteredList: MutableList<Restaurant> = mutableListOf()

                        for (restaurant in restaurantList) {
                            if (restaurant.name?.toLowerCase()!!.contains(p0.toString().toLowerCase())) {
                                filteredList.add(restaurant)
                            }
                        }

                        if (filteredList.size == 0) {
                            noResult.visibility = View.VISIBLE
                            noResultIcon.visibility = View.VISIBLE
                        } else {
                            noResult.visibility = View.INVISIBLE
                            noResultIcon.visibility = View.INVISIBLE
                        }

                        adapter.setRestaurants(filteredList)
                    }
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        restaurantViewModel.noInternet.observe(this, Observer { noInternet ->
            if (noInternet == null)
                return@Observer

            if (noInternet) {
                openFragment(NoInternetFragment.newInstance(), NO_INTERNET_CONNECTION)
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