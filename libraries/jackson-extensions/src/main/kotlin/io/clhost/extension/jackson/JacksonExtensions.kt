package io.clhost.extension.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import java.text.SimpleDateFormat

val configJackson: ObjectMapper.() -> Unit = {
    enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
    enable(MapperFeature.AUTO_DETECT_IS_GETTERS)
    enable(MapperFeature.AUTO_DETECT_FIELDS)
    enable(MapperFeature.AUTO_DETECT_GETTERS)

    disable(SerializationFeature.INDENT_OUTPUT)
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

    dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    setSerializationInclusion(JsonInclude.Include.NON_NULL)

    registerModule(JavaTimeModule())
    registerModule(Jdk8Module())
    registerModule(KotlinModule())
}

val standardObjectMapper = ObjectMapper().apply { configJackson() }

fun jsonEncode(obj: Any?): String = standardObjectMapper.writeValueAsString(obj)

inline fun <reified T> jsonDecode(json: String): T = standardObjectMapper.readValue(json, jacksonTypeRef<T>())
