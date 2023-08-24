package io.clhost.extension.web

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

class ContentReadingRequestWrapper(
    request: HttpServletRequest
) : HttpServletRequestWrapper(request) {

    private val body: ByteArray
    private val delegatingServletInputStream: DelegatingServletInputStream
    private val reader: BufferedReader

    init {
        body = readFully(request)
        delegatingServletInputStream = DelegatingServletInputStream(ByteArrayInputStream(body))
        reader = BufferedReader(InputStreamReader(ByteArrayInputStream(body)))
    }

    fun getContentAsByteArray() = body

    @Throws(IOException::class)
    override fun getInputStream() = delegatingServletInputStream

    @Throws(IOException::class)
    override fun getReader() = reader

    @Throws(IOException::class)
    private fun readFully(request: HttpServletRequest): ByteArray {
        val from = request.inputStream
        val to = ByteArrayOutputStream(1024)
        val buf = ByteArray(1024)
        while (true) {
            val r = from.read(buf)
            if (r == -1) {
                break
            }
            to.write(buf, 0, r)
        }
        return to.toByteArray()
    }
}

class DelegatingServletInputStream(
    private val inputStream: InputStream
) : ServletInputStream() {
    override fun read(): Int = inputStream.read()
    override fun isReady(): Boolean = throw IllegalStateException("Not implemented")
    override fun isFinished(): Boolean = throw IllegalStateException("Not implemented")
    override fun setReadListener(listener: ReadListener?) = throw IllegalStateException("Not implemented")
}
