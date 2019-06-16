package si.um.feri.restaurantsapp.retrofit.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import kotlinx.android.parcel.Parcelize

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "idRestaurant",
    "name",
    "description",
    "photoUrl",
    "address",
    "latitude",
    "longitude",
    "currentRating",
    "type"
)
@Parcelize
data class RestaurantJacksonModel(
    @JsonProperty("idRestaurant")
    val idRestaurant: Int?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    @JsonProperty("address")
    val address: String?,
    @JsonProperty("latitude")
    val latitude: Double?,
    @JsonProperty("longitude")
    val longitude: Double?,
    @JsonProperty("currentRating")
    val currentRating: Double?,
    @JsonProperty("type")
    val type: String?
): Parcelable