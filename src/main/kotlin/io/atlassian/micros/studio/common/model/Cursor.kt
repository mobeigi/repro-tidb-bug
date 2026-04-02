package io.atlassian.micros.studio.common.model

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.Base64

interface Cursor {
    companion object {
        inline fun <reified T> fromCursorString(
            objectMapper: ObjectMapper,
            cursorString: String,
        ): T {
            val json = String(Base64.getDecoder().decode(cursorString))
            return objectMapper.readValue(json, T::class.java)
        }
    }

    fun toCursorString(objectMapper: ObjectMapper): String {
        val json = objectMapper.writeValueAsString(this)
        return Base64.getEncoder().encodeToString(json.toByteArray())
    }
}
