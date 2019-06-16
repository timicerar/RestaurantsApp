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
import kotlinx.android.synthetic.main.rv_opinion_item.view.*
import org.apache.commons.lang.StringEscapeUtils
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.dialog.RemoveCommentDialog
import si.um.feri.restaurantsapp.fragments.RestaurantFragment
import si.um.feri.restaurantsapp.models.CommentWithRating

private const val RESTAURANT_FRAGMENT = "RESTAURANT_FRAGMENT"

class OpinionsAdapter(
    private val context: Context
) : RecyclerView.Adapter<OpinionsAdapter.OpinionsViewHolder>(), RemoveCommentDialog.Listener {

    override fun returnData(commentWithRating: CommentWithRating) {
        this.opinions.remove(commentWithRating)
        notifyDataSetChanged()
    }

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var opinions = arrayListOf<CommentWithRating>()

    internal fun setOpinions(opinions: List<CommentWithRating>) {
        val arrayList = arrayListOf<CommentWithRating>()
        arrayList.addAll(opinions)
        this.opinions = arrayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): OpinionsViewHolder {
        val itemView: View = layoutInflater.inflate(R.layout.rv_opinion_item, viewGroup, false)
        return OpinionsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OpinionsViewHolder, position: Int) {
        val tempOpinion = opinions[position]
        setImage(tempOpinion.comment.restaurant?.photoUrl, holder)
        holder.opinionRestaurantName.text = tempOpinion.comment.restaurant?.name
        holder.opinionRestaurantName.setOnClickListener {
            goToFragment(tempOpinion.comment.restaurant?.idRestaurant!!)
        }
        holder.opinionRatingBar.rating = tempOpinion.rating.value!!.toFloat()
        holder.opinionCommentText.text = StringEscapeUtils.unescapeJava(tempOpinion.comment.text)
        holder.opinionCommentDate.text = tempOpinion.comment.timeOfPublication.toString()
        holder.opinionRemoveComment?.setOnClickListener {
            val deleteCommentDialog = RemoveCommentDialog().newInstance(tempOpinion)
            deleteCommentDialog.setListener(this)
            deleteCommentDialog.show((context as AppCompatActivity).supportFragmentManager, "delete_fragment")
        }
    }

    override fun getItemCount() = opinions.size

    inner class OpinionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val opinionRestaurantImage: ImageView = itemView.opinionRestaurantImage
        val opinionRestaurantName: TextView = itemView.opinionRestaurantName
        val opinionRatingBar: RatingBar = itemView.opinionRatingBar
        val opinionCommentText: TextView = itemView.opinionCommentText
        val opinionCommentDate: TextView = itemView.opinionCommentDate
        val opinionRemoveComment: ImageView? = itemView.opinionRemoveComment
    }

    private fun setImage(
        url: String?,
        holder: OpinionsViewHolder
    ) {
        if (url != "" && url != null) {
            Picasso
                .with(context)
                .load(url)
                .error(R.color.colorGray)
                .into(holder.opinionRestaurantImage)
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