package si.um.feri.restaurantsapp.retrofit.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("idDay", "name")
data class DayJacksonModel(
    @JsonProperty("idDay")
    val idDay: Int?,
    @JsonProperty("name")
    val name: String?
)