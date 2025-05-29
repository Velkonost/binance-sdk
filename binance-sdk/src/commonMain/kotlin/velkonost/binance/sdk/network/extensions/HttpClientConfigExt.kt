package velkonost.binance.sdk.network.extensions

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*

internal fun HttpClientConfig<*>.httpTimeout(block: HttpTimeoutConfig.() -> Unit) =
    install(HttpTimeout, block)

internal fun HttpClientConfig<*>.contentNegotiation(block: ContentNegotiationConfig.() -> Unit) =
    install(ContentNegotiation, block)

internal fun HttpClientConfig<*>.webSockets(block: WebSockets.Config.() -> Unit) =
    install(WebSockets, block)

internal fun HttpClientConfig<*>.requestRetry(block: HttpRequestRetryConfig.() -> Unit) =
    install(HttpRequestRetry, block)