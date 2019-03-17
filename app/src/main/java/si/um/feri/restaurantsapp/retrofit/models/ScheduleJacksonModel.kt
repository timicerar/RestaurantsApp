package si.um.feri.restaurantsapp.retrofit.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import si.um.feri.restaurantsapp.retrofit.models.deserializers.LocalTimeDeserializer
import si.um.feri.restaurantsapp.retrofit.models.serializers.LocalTimeSerializer
import java.time.LocalTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("idSchedule", "orderBy", "startTime", "endTime", "day", "restaurant")
data class ScheduleJacksonModel(
    @JsonProperty("idSchedule")
    val idSchedule: Int?,
    @JsonProperty("orderBy")
    val orderBy: Int?,
    @JsonProperty("startTime")
    @JsonDeserialize(using = LocalTimeDeserializer::class)
    @JsonSerialize(using = LocalTimeSerializer::class)
    val startTime: LocalTime?,
    @JsonProperty("endTime")
    @JsonDeserialize(using = LocalTimeDeserializer::class)
    @JsonSerialize(using = LocalTimeSerializer::class)
    val endTime: LocalTime?,
    @JsonProperty("day")
    val day: DayJacksonModel?,
    @JsonProperty("restaurant")
    val restaurant: RestaurantJacksonModel?
)