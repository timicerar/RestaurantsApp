package si.um.feri.restaurantsapp.retrofit.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

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
)