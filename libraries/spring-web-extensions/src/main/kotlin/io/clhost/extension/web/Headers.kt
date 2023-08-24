package io.clhost.extension.web

import java.util.Collections
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

fun readHeaders(request: HttpServletRequest): String {
    val sb = StringBuilder("{")
    val headerNames = request.headerNames
    while (headerNames.hasMoreElements()) {
        val name = headerNames.nextElement()
        for (value in Collections.list(request.getHeaders(name))) {
            sb.append("\"$name\":\"$value\",")
        }
    }
    return sb.append("}").toString()
}

fun readHeaders(response: HttpServletResponse): String {
    val sb = StringBuilder("{")
    for (name in response.headerNames) {
        for (value in response.getHeaders(name)) {
            sb.append("\"$name\":\"$value\",")
        }
    }
    return sb.append("}").toString()
}
