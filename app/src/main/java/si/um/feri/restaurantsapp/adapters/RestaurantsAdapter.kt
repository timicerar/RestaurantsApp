package si.um.feri.restaurantsapp.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_restaurant_item.view.*
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.fragments.RestaurantFragment
import si.um.feri.restaurantsapp.room.models.Restaurant

private const val RESTAURANT_FRAGMENT = "RESTAURANT_FRAGMENT"

class RestaurantsAdapter(private val context: Context) :
    RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var restaurants = emptyList<Restaurant>()

    internal fun setRestaurants(restaurants: List<Restaurant>) {
        this.restaurants = restaurants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val itemView: View = layoutInflater.inflate(R.layout.rv_restaurant_item, viewGroup, false)
        return RestaurantsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {
        val tempRestaurant = restaurants[position]
        holder.restaurantName.text = tempRestaurant.name
        holder.restaurantAddress.text = tempRestaurant.address
        holder.restaurantRating.rating = tempRestaurant.currentRating!!.toFloat()

        if (tempRestaurant.isFavorite) {
            holder.restaurantFavNoFill.visibility = View.INVISIBLE
            holder.restaurantFavFill.visibility = View.VISIBLE
        } else {
            holder.restaurantFavNoFill.visibility = View.VISIBLE
            holder.restaurantFavFill.visibility = View.INVISIBLE
        }

        setImage(tempRestaurant.photoUrl, holder)

        holder.itemView.setOnClickListener {
            goToFragment(tempRestaurant.restaurantId!!)
        }
    }

    override fun getItemCount() = restaurants.size

    inner class RestaurantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantImage: ImageView = itemView.homeRestaurantImage
        val restaurantName: TextView = itemView.homeRestaurantName
        val restaurantAddress: TextView = itemView.homeAddress
        val restaurantRating: RatingBar = itemView.homeRatingBar
        val restaurantFavNoFill: ImageView = itemView.homeFavoriteNoFill
        val restaurantFavFill: ImageView = itemView.homeFavoriteFill
    }

    private fun setImage(
        url: String?,
        holder: RestaurantsViewHolder
    ) {
        if (url != "" && url != null) {
            Picasso
                .with(context)
                .load(url)
                .error(R.color.colorGray)
                .into(holder.restaurantImage)
        }
    }

    private fun goToFragment(restaurantId: Int, canGoBack: Boolean = true) {
        val bundle = Bundle()
        bundle.putInt("restaurantId", restaurantId)

        val restaurantFragment = RestaurantFragment.newInstance()
        restaurantFragment.arguments = bundle

        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, restaurantFragment, RESTAURANT_FRAGMENT)
            .apply { if (canGoBack) addToBackStack(null) }
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}