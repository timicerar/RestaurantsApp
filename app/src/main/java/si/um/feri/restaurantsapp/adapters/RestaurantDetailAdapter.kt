package si.um.feri.restaurantsapp.adapters

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.rv_restaurant_comment.view.*
import kotlinx.android.synthetic.main.rv_restaurant_header.view.*
import kotlinx.android.synthetic.main.rv_restaurant_info.view.*
import kotlinx.coroutines.withContext
import org.apache.commons.lang.StringEscapeUtils
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.dialog.ScheduleDialog
import si.um.feri.restaurantsapp.fragments.AddCommentAndRatingFragment
import si.um.feri.restaurantsapp.models.*
import si.um.feri.restaurantsapp.room.models.Restaurant


private const val RESTAURANT = 0
private const val LOCATION = 1
private const val DESCRIPTION = 2
private const val SCHEDULE_INFO = 3
private const val COMMENTS_INFO = 4
private const val COMMENT = 5

private const val ADD_COMMENT_AND_RATING = "ADD_COMMENT_AND_RATING"

class RestaurantDetailAdapter(
    private val context: Context,
    private val favoriteCallBack: (restaurant: Restaurant) -> Unit,
    private val removeFavoriteCallBack: (restaurant: Restaurant) -> Unit
) :
    RecyclerView.Adapter<RestaurantDetailAdapter.RestaurantDetailViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var restaurantDetails = emptyList<Any>()

    internal fun setRestaurantDetails(restaurantDetails: List<Any>) {
        this.restaurantDetails = restaurantDetails
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            restaurantDetails[position] is Restaurant -> RESTAURANT
            restaurantDetails[position] is Location -> LOCATION
            restaurantDetails[position] is Description -> DESCRIPTION
            restaurantDetails[position] is ScheduleInfo -> SCHEDULE_INFO
            restaurantDetails[position] is CommentInfo -> COMMENTS_INFO
            else -> COMMENT
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RestaurantDetailViewHolder {
        val itemView: View = when (viewType) {
            RESTAURANT -> layoutInflater.inflate(R.layout.rv_restaurant_header, viewGroup, false)
            LOCATION -> layoutInflater.inflate(R.layout.rv_restaurant_info, viewGroup, false)
            DESCRIPTION -> layoutInflater.inflate(R.layout.rv_restaurant_info, viewGroup, false)
            SCHEDULE_INFO -> layoutInflater.inflate(R.layout.rv_restaurant_info, viewGroup, false)
            COMMENTS_INFO -> layoutInflater.inflate(R.layout.rv_restaurant_info, viewGroup, false)
            else -> layoutInflater.inflate(R.layout.rv_restaurant_comment, viewGroup, false)
        }

        return RestaurantDetailViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RestaurantDetailViewHolder, position: Int) {
        when {
            restaurantDetails[position] is Restaurant -> {
                val tempValue = restaurantDetails[position] as Restaurant
                Picasso
                    .with(context)
                    .load(tempValue.photoUrl)
                    .error(R.color.colorGray)
                    .into(holder.restaurantHeaderImage)
                holder.restaurantHeaderName?.text = tempValue.name

                val number: Double = tempValue.currentRating!!
                val number3digits: Double = Math.round(number * 1000.0) / 1000.0
                val number2digits: Double = Math.round(number3digits * 100.0) / 100.0
                val solution: Double = Math.round(number2digits * 10.0) / 10.0
                holder.restaurantHeaderRating?.text = solution.toString()

                holder.restaurantHeaderRatingBar?.rating = tempValue.currentRating!!.toFloat()

                if (tempValue.isFavorite) {
                    holder.restaurantHeaderFavNoFill?.visibility = View.INVISIBLE
                    holder.restaurantHeaderFavFill?.visibility = View.VISIBLE
                } else {
                    holder.restaurantHeaderFavNoFill?.visibility = View.VISIBLE
                    holder.restaurantHeaderFavFill?.visibility = View.INVISIBLE
                }

                holder.restaurantHeaderRatingBar?.setOnRatingBarChangeListener { _, rating, _ ->
                    goToFragmentAddCommentRating(tempValue.restaurantId!!, rating)
                }

                holder.restaurantHeaderFavNoFill?.setOnClickListener {
                    holder.restaurantHeaderFavNoFill.visibility = View.INVISIBLE
                    holder.restaurantHeaderFavFill?.visibility = View.VISIBLE
                    favoriteCallBack(tempValue)
                }

                holder.restaurantHeaderFavFill?.setOnClickListener {
                    holder.restaurantHeaderFavNoFill?.visibility = View.VISIBLE
                    holder.restaurantHeaderFavFill.visibility = View.INVISIBLE
                    removeFavoriteCallBack(tempValue)
                }
            }
            restaurantDetails[position] is Location -> {
                val tempValue = restaurantDetails[position] as Location
                holder.restaurantInfoImage?.setImageDrawable(context.getDrawable(R.drawable.ic_location_24dp))
                holder.restaurantInfoText?.text = tempValue.address
            }
            restaurantDetails[position] is Description -> {
                val tempValue = restaurantDetails[position] as Description
                holder.restaurantInfoImage?.setImageDrawable(context.getDrawable(R.drawable.ic_description_24dp))
                holder.restaurantInfoText?.text = StringEscapeUtils.unescapeJava(tempValue.description)
            }
            restaurantDetails[position] is ScheduleInfo -> {
                val tempValue = restaurantDetails[position] as ScheduleInfo
                holder.restaurantInfoImage?.setImageDrawable(context.getDrawable(R.drawable.ic_schedule_24dp))
                holder.restaurantInfoText?.text = tempValue.name
                holder.itemView.setOnClickListener {
                    val scheduleDialog = ScheduleDialog().newInstance(tempValue.fullSchedule)
                    scheduleDialog.show((context as AppCompatActivity).supportFragmentManager, "schedule_fragment")
                }
            }
            restaurantDetails[position] is CommentInfo -> {
                val tempValue = restaurantDetails[position] as CommentInfo
                holder.restaurantInfoImage?.setImageDrawable(context.getDrawable(R.drawable.ic_comment_orange_24dp))
                holder.restaurantInfoText?.text = tempValue.name
            }
            restaurantDetails[position] is CommentWithRating -> {
                val tempValue = restaurantDetails[position] as CommentWithRating
                Picasso
                    .with(context)
                    .load(tempValue.comment.user?.photoUrl)
                    .error(R.color.colorGray)
                    .into(holder.commentImage)
                holder.commentUser?.text = tempValue.comment.user?.nameSurname
                holder.commentRatingBar?.rating = tempValue.rating.value!!.toFloat()
                holder.commentText?.text = StringEscapeUtils.unescapeJava(tempValue.comment.text)
                holder.commentDate?.text = tempValue.comment.timeOfPublication.toString()
            }
        }
    }

    inner class RestaurantDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantHeaderImage: ImageView? = itemView.restaurantImage
        val restaurantHeaderName: TextView? = itemView.restaurantName
        val restaurantHeaderRating: TextView? = itemView.restaurantRating
        val restaurantHeaderRatingBar: RatingBar? = itemView.restaurantRatingBar
        val restaurantHeaderFavNoFill: ImageView? = itemView.restaurantFavoriteNoFill
        val restaurantHeaderFavFill: ImageView? = itemView.restaurantFavoriteFill

        val restaurantInfoImage: ImageView? = itemView.restaurantInfoImage
        val restaurantInfoText: TextView? = itemView.restaurantInfoText

        val commentImage: ImageView? = itemView.commentUserProfilePicture
        val commentUser: TextView? = itemView.commentUserName
        val commentRatingBar: RatingBar? = itemView.commentRatingBar
        val commentText: TextView? = itemView.commentText
        val commentDate: TextView? = itemView.commentDate
    }

    override fun getItemCount() = restaurantDetails.size

    private fun goToFragmentAddCommentRating(restaurantId: Int, rating: Float, canGoBack: Boolean = true) {
        val bundle = Bundle()
        bundle.putInt("restaurantId", restaurantId)
        bundle.putFloat("ratingValue", rating)

        val addCommentAndRatingFragment = AddCommentAndRatingFragment.newInstance()
        addCommentAndRatingFragment.arguments = bundle

        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, addCommentAndRatingFragment, ADD_COMMENT_AND_RATING)
            .apply { if (canGoBack) addToBackStack(null) }
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

}