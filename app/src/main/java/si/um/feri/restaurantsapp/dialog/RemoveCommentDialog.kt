package si.um.feri.restaurantsapp.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.TypedValue
import android.widget.TextView
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.models.CommentWithRating
import si.um.feri.restaurantsapp.viewmodels.RestaurantViewModel

class RemoveCommentDialog : DialogFragment() {

    private var mListener: Listener? = null

    fun setListener(listener: Listener) {
        mListener = listener
    }

    interface Listener {
        fun returnData(commentWithRating: CommentWithRating)
    }

    fun newInstance(commentAndRating: CommentWithRating): RemoveCommentDialog {
        val removeCommentDialog = RemoveCommentDialog()
        val bundle = Bundle()
        bundle.putParcelable("commentWithRating", commentAndRating)
        removeCommentDialog.arguments = bundle
        return removeCommentDialog
    }

    private lateinit var restaurantViewModel: RestaurantViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val title = TextView(requireContext())

            title.text = "Brisanje komentarja"
            title.setBackgroundColor(Color.rgb(255, 255, 255))
            title.setTextColor(Color.BLACK)
            title.setPadding(64, 30, 64, 10)
            title.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                resources.getDimension(R.dimen.dialog_title_text_size)
            )

            builder.setCustomTitle(title)

            val bundle = arguments
            val commentWithRating = bundle?.getParcelable<CommentWithRating>("commentWithRating")

            val content = TextView(requireContext())
            content.text = "Ste prepričani, da želite trajno odstraniti to mnenje?"
            content.setPadding(64, 30, 64, 10)
            content.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                resources.getDimension(R.dimen.dialog_title_text_size)
            )

            builder.setView(content)

            builder.setNegativeButton(
                "Ne"
            ) { dialog, _ -> dialog.dismiss() }

            builder.setPositiveButton(
                "Da"
            ) { dialog, _ ->
                restaurantViewModel.removeOpinion(commentWithRating?.comment?.idComment)
                mListener!!.returnData(commentWithRating!!)
                dialog.dismiss()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}