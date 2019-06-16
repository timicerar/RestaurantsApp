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
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.editTextSearchRestaurants
import kotlinx.android.synthetic.main.fragment_home.noResult
import kotlinx.android.synthetic.main.fragment_home.noResultIcon
import kotlinx.android.synthetic.main.fragment_opinions.*
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.adapters.OpinionsAdapter
import si.um.feri.restaurantsapp.adapters.RestaurantsAdapter
import si.um.feri.restaurantsapp.dialog.RemoveCommentDialog
import si.um.feri.restaurantsapp.models.CommentWithRating
import si.um.feri.restaurantsapp.room.models.Restaurant
import si.um.feri.restaurantsapp.viewmodels.LoginViewModel
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

private const val NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION"

class OpinionsFragment : Fragment() {

    companion object {
        fun newInstance(): OpinionsFragment = OpinionsFragment()
    }

    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_opinions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.opinions_rv)
        val adapter = OpinionsAdapter(
            requireContext()
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loginViewModel.getCurrentUser()
        loginViewModel.currentUser.observe(this, Observer { user ->
            if (user == null)
                return@Observer

            restaurantViewModel.getUsersOpinions(user.uid)
            restaurantViewModel.usersOpinions.observe(this, Observer { usersOpinions ->
                val sortedList = usersOpinions?.sortedBy { it.comment.restaurant?.name }

                if (sortedList!!.isNotEmpty()) {
                    noOpinions.visibility = View.INVISIBLE
                    noOpinionsText.visibility = View.INVISIBLE
                    adapter.setOpinions(sortedList)
                } else {
                    adapter.setOpinions(emptyList())
                    noOpinions.visibility = View.VISIBLE
                    noOpinionsText.visibility = View.VISIBLE
                }
            })
        })

        editTextSearchOpinions.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                restaurantViewModel.usersOpinions.observe(requireActivity(), Observer { usersOpinions ->
                    if (usersOpinions == null)
                        return@Observer

                    val sortedList = usersOpinions.sortedBy { it.comment.restaurant?.name }

                    if (sortedList.isNotEmpty()) {
                        noOpinions.visibility = View.INVISIBLE
                        noOpinionsText.visibility = View.INVISIBLE

                        sortedList.let {
                            val filteredList: MutableList<CommentWithRating> = mutableListOf()

                            for (opinion in sortedList) {
                                if (opinion.comment.restaurant?.name?.toLowerCase()!!.contains(p0.toString().toLowerCase())) {
                                    filteredList.add(opinion)
                                }
                            }

                            if (filteredList.size == 0) {
                                opinionsNoResultText.visibility = View.VISIBLE
                                opinionsNoResultIcon.visibility = View.VISIBLE
                            } else {
                                opinionsNoResultText.visibility = View.INVISIBLE
                                opinionsNoResultIcon.visibility = View.INVISIBLE
                            }

                            adapter.setOpinions(filteredList)
                        }
                    } else {
                        adapter.setOpinions(emptyList())
                        noOpinions.visibility = View.VISIBLE
                        noOpinionsText.visibility = View.VISIBLE
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