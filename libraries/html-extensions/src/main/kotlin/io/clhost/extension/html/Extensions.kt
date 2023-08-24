package io.clhost.extension.html

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import java.lang.StringBuilder

fun extractTextFromHtml(html: String?, replacementKey: String? = null): String? {
    if (html == null) return null

    val text = StringBuilder()

    val handler = KsoupHtmlHandler
        .Builder()
        .onText {
            if (replacementKey == null) text.append(it)
            if (replacementKey != null && it.contains(replacementKey)) text.append(it.replace(replacementKey, ""))
        }
        .build()

    KsoupHtmlParser(handler)
        .apply { write(html) }
        .end()

    return if (text.isBlank()) null else text.toString()
}
