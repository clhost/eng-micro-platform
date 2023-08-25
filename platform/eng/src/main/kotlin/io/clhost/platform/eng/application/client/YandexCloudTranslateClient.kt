package io.clhost.platform.eng.application.client

import io.clhost.extension.ktor.client.applicationJson
import io.clhost.extension.ktor.client.plugin.label
import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
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
    @Value("\${client.yandex-cloud-translate.url}")
    private val url: String,

    @Value("\${client.yandex-cloud-translate.folder-id}")
    private val folderId: String,

    @Value("\${client.yandex-cloud-translate.iam-token}")
    private val iamToken: String,

    @Qualifier("yandexCloudTranslateKtorClient")
    private val client: HttpClient
) {

    fun translateEnToRu(word: String) = runBlockingWithPreservedCorrelationId {
        val request = TranslationRequest(
            folderId = folderId,
            texts = listOf(word),
            sourceLanguageCode = "en",
            targetLanguageCode = "ru"
        )
        client.post(url) {
            applicationJson()
            setBody(request)
            label("translateEnToRu")
            bearerAuth(iamToken)
        }.body<TranslationResponse>().translations.firstOrNull()?.text
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
) {
    data class Translation(
        val text: String
    )
}