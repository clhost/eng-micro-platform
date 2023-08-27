package io.clhost.platform.eng.application.yandex

import io.clhost.extension.ktor.client.applicationJson
import io.clhost.extension.ktor.client.plugin.label
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class YandexIAMTokenProvider(
    @Qualifier("yandexCloudKtorClient")
    private val client: HttpClient,

    @Value("\${client.yandex-cloud.tokens-url}")
    private val url: String,

    @Value("\${client.yandex-cloud.oauth-token}")
    private val oauthToken: String,

    private val redisTemplate: RedisTemplate<String, String>
) {

    private val logger = LoggerFactory.getLogger(YandexIAMTokenProvider::class.java)

    companion object {
        const val iamTokenKey = "iam_token"
    }

    fun getIAMToken() = redisTemplate.opsForValue().get(iamTokenKey)
        ?: throw Exception("IAM token not found in Redis")

    @Scheduled(initialDelay = 1_000, fixedDelay = 60_000_000)
    fun refreshToken() {
        redisTemplate.opsForValue()[iamTokenKey] = getNewIAMToken()
        logger.info("Successfully refreshed token")
    }

    private fun getNewIAMToken() = runBlocking {
        client.post(url) {
            label("getNewIAMToken")
            applicationJson()
            setBody(Exchange(oauthToken))
        }.body<IAMToken>().iamToken
    }

    data class IAMToken(val iamToken: String)
    data class Exchange(val yandexPassportOauthToken: String)
}
