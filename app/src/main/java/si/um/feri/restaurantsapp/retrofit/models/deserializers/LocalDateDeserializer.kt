package si.um.feri.restaurantsapp.retrofit.models.deserializers

import android.os.Build
import android.support.annotation.RequiresApi
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

import java.io.IOException
import java.time.LocalDate

class LocalDateDeserializer @RequiresApi(api = Build.VERSION_CODES.O) private constructor() :
    StdDeserializer<LocalDate>(LocalDate::class.java) {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext): LocalDate {
        return LocalDate.parse(jsonParser.readValueAs(String::class.java))
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
