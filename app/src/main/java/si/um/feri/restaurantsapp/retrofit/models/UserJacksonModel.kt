package si.um.feri.restaurantsapp.retrofit.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.android.parcel.Parcelize
import si.um.feri.restaurantsapp.retrofit.models.deserializers.LocalDateDeserializer
import si.um.feri.restaurantsapp.retrofit.models.serializers.LocalDateSerializer
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("idUser", "googleUserId", "nameSurname", "birthday", "gender", "email", "photoUrl")
@Parcelize
data class UserJacksonModel(
    @JsonProperty("idUser")
    val idUser: Int?,
    @JsonProperty("googleUserId")
    val googleUserId: String?,
    @JsonProperty("nameSurname")
    val nameSurname: String?,
    @JsonProperty("birthday")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val birthday: LocalDate?,
    @JsonProperty("gender")
    val gender: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("photoUrl")
    val photoUrl: String?
): Parcelable