package si.um.feri.restaurantsapp.retrofit.models.serializers

import android.os.Build
import android.support.annotation.RequiresApi
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeSerializer @RequiresApi(api = Build.VERSION_CODES.O)
constructor() : StdSerializer<LocalDateTime>(LocalDateTime::class.java) {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    override fun serialize(value: LocalDateTime, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
