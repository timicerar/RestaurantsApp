package si.um.feri.restaurantsapp.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_comment_rating.*
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.viewmodels.LoginViewModel
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

private const val NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION"

class AddCommentAndRatingFragment : Fragment() {

    companion object {
        fun newInstance(): AddCommentAndRatingFragment = AddCommentAndRatingFragment()
    }

    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_comment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        val bundle = arguments
        val restaurantId = bundle?.getInt("restaurantId")
        val rating = bundle?.getFloat("ratingValue")

        if (restaurantId != null && rating != null) {
            restaurantViewModel.getRestaurantDetails(restaurantId)
            restaurantViewModel.restaurantDetails.observe(this, Observer { restaurant ->
                Picasso
                    .with(context)
                    .load(restaurant?.photoUrl)
                    .error(R.color.colorGray)
                    .into(rateRestaurantImage)
                rateRestaurantName.text = restaurant?.name
                rateRestaurantRatingBar.rating = rating
            })

            rateButtonOpinion.setOnClickListener {
                if (rateCommentText.text.isNotEmpty() && (rateRestaurantRatingBar.rating > 0 && rateRestaurantRatingBar.rating <= 5)) {
                    loginViewModel.getCurrentUser()
                    loginViewModel.currentUser.observe(this, Observer { currentUser ->
                        if (currentUser == null)
                            return@Observer

                        restaurantViewModel.addRestaurantOpinion(
                            rateCommentText.text.toString(),
                            rateRestaurantRatingBar.rating,
                            currentUser.email!!,
                            restaurantId
                        )

                        restaurantViewModel.firstComment.observe(this, Observer {
                            println(it!!)
                        })

                        Toast.makeText(requireContext(), "Hvala za vaše mnenje!", Toast.LENGTH_LONG).show()

                        restaurantViewModel.loadingData.observe(this, Observer { loading ->
                            if (!loading!!)
                                goBackOnSubmit()
                        })
                    })
                } else {
                    if (rateCommentText.text.isEmpty() || rateRestaurantRatingBar.rating <= 0 || rateRestaurantRatingBar.rating > 5)
                        Toast.makeText(
                            requireContext(),
                            "Izberite oceno med 0 in 5 ter napišite svoje mnenje o restavraciji.",
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
        }

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

    private fun goBackOnSubmit() {
        (context as AppCompatActivity).onBackPressed()
    }
}