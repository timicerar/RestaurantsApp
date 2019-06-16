package si.um.feri.restaurantsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import si.um.feri.restaurantsapp.retrofit.models.CommentJacksonModel
import si.um.feri.restaurantsapp.retrofit.models.RatingJacksonModel

@Parcelize
data class CommentWithRating (
    val comment: CommentJacksonModel,
    val rating: RatingJacksonModel
): Parcelable