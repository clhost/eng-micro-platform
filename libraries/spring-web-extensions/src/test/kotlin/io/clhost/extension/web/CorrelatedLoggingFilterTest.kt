package io.clhost.extension.web

import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@SpringBootTest(classes = [TestApplication::class])
class CorrelatedLoggingFilterTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @AfterEach
    fun clear() = clearAllMocks()

    @Test
    fun `should generate and correlate get request with correlationId`() {
        val response = mockMvc.get("/get").andExpect {
            status { isOk() }
        }.andReturn().response

        assertNotNull(response.getHeader(CorrelationId.headerName))
    }

    @Test
    fun `should set and correlate get request with correlationId`() {
        val response = mockMvc.get("/get") {
            header(CorrelationId.headerName, "test")
        }.andExpect {
            status { isOk() }
        }.andReturn().response

        assertEquals(response.getHeader(CorrelationId.headerName), "test")
    }

    @Test
    fun `should generate and correlate post request with correlationId`() {
        val response = mockMvc.post("/post") {
            contentType = MediaType.APPLICATION_JSON
            content = "{\"key\": \"key\", \"value\": \"value\"}"
        }.andExpect {
            status { isOk() }
        }.andReturn().response

        assertNotNull(response.getHeader(CorrelationId.headerName))
    }

    @Test
    fun `should set and correlate post request with correlationId`() {
        val response = mockMvc.post("/post") {
            header(CorrelationId.headerName, "test")
            contentType = MediaType.APPLICATION_JSON
            content = "{\"key\": \"key\", \"value\": \"value\"}"
        }.andExpect {
            status { isOk() }
        }.andReturn().response

        assertEquals(response.getHeader(CorrelationId.headerName), "test")
    }

    @Test
    fun `should generate and correlate post request with correlationId when http 4xx`() {
        val response = mockMvc.post("/post") {
            contentType = MediaType.APPLICATION_JSON
            content = "{\"garbage\": \"key\", \"value\": \"value\"}"
        }.andExpect {
            status { isBadRequest() }
        }.andReturn().response

        assertNotNull(response.getHeader(CorrelationId.headerName))
    }

    @Test
    fun `should set and correlate post request with correlationId when http 4xx`() {
        val response = mockMvc.post("/post") {
            header(CorrelationId.headerName, "test")
            contentType = MediaType.APPLICATION_JSON
            content = "{\"garbage\": \"key\", \"value\": \"value\"}"
        }.andExpect {
            status { isBadRequest() }
        }.andReturn().response

        assertEquals(response.getHeader(CorrelationId.headerName), "test")
    }
}
