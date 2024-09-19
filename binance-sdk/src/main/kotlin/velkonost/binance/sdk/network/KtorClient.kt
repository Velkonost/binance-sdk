package velkonost.binance.sdk.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import velkonost.binance.sdk.network.extensions.contentNegotiation
import velkonost.binance.sdk.network.extensions.httpTimeout
import velkonost.binance.sdk.network.extensions.webSockets

internal const val KTOR_REQUEST_TIMEOUT_MILLIS = 30_000L

@OptIn(ExperimentalSerializationApi::class)
internal fun ktorClient(url: String, headers: List<Pair<String, String>>? = null): HttpClient {
    val ktorClient = withPlatformEngine {
        Logging {
            logger = Logger.SIMPLE
            level = LogLevel.NONE
        }

        httpTimeout {
            requestTimeoutMillis = KTOR_REQUEST_TIMEOUT_MILLIS
            socketTimeoutMillis = KTOR_REQUEST_TIMEOUT_MILLIS * 2
            connectTimeoutMillis = KTOR_REQUEST_TIMEOUT_MILLIS
        }

        contentNegotiation {
            json(
                Json {
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }

        webSockets {
            pingInterval = KTOR_REQUEST_TIMEOUT_MILLIS
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        defaultRequest {
            url(url)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            headers?.let { headersList ->
                headersList.forEach { header(it.first, it.second) }
            }
        }
    }

    return ktorClient
}