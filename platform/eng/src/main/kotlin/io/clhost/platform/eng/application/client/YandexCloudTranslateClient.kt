package io.clhost.platform.eng.application.client

import io.clhost.extension.ktor.client.applicationJson
import io.clhost.extension.ktor.client.plugin.label
import io.clhost.platform.eng.application.WithSource
import io.clhost.platform.eng.application.yandex.YandexIAMTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class YandexCloudTranslateClient(
    @Value("\${client.yandex-cloud.translate-url}")
    private val url: String,

    @Value("\${client.yandex-cloud.folder-id}")
    private val folderId: String,

    @Qualifier("yandexCloudKtorClient")
    private val client: HttpClient,

    private val yandexIAMTokenProvider: YandexIAMTokenProvider
) {

    suspend fun translateEnToRu(word: String): Translation? {
        val iamToken = yandexIAMTokenProvider.getIAMToken().iamToken
        val request = TranslationRequest(
            folderId = folderId,
            texts = listOf(word),
            sourceLanguageCode = "en",
            targetLanguageCode = "ru"
        )
        return client.post(url) {
            applicationJson()
            setBody(request)
            label("translateEnToRu")
            bearerAuth(iamToken)
        }.body<TranslationResponse>().translations.firstOrNull()
    }
}

data class TranslationRequest(
    val folderId: String,
    val texts: List<String>,
    val sourceLanguageCode: String,
    val targetLanguageCode: String
)

data class TranslationResponse(
    val translations: List<Translation>
)

data class Translation(
    val text: String,
    override val source: String = "yandex.cloud"
) : WithSource
