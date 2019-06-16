package si.um.feri.restaurantsapp.retrofit.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.android.parcel.Parcelize
import si.um.feri.restaurantsapp.retrofit.models.deserializers.LocalDateTimeDeserializer
import si.um.feri.restaurantsapp.retrofit.models.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("idRating", "value", "timeOfPublication", "restaurant", "user")
@Parcelize
data class RatingJacksonModel(
    @JsonProperty("idRating")
    val idRating: Int?,
    @JsonProperty("value")
    val value: Double?,
    @JsonProperty("timeOfPublication")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val timeOfPublication: LocalDateTime?,
    @JsonProperty("restaurant")
    val restaurant: RestaurantJacksonModel?,
    @JsonProperty("user")
    val user: UserJacksonModel?
): Parcelable