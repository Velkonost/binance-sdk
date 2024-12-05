package velkonost.binance.sdk.network.extensions

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.*
import io.ktor.client.plugins.HttpTimeout.HttpTimeoutCapabilityConfiguration
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.*

internal fun HttpClientConfig<*>.httpTimeout(block: HttpTimeoutCapabilityConfiguration.() -> Unit) =
    install(HttpTimeout, block)

internal fun HttpClientConfig<*>.contentNegotiation(block: ContentNegotiation.Config.() -> Unit) =
    install(ContentNegotiation, block)

internal fun HttpClientConfig<*>.webSockets(block: WebSockets.Config.() -> Unit) =
    install(WebSockets, block)

internal fun HttpClientConfig<*>.requestRetry(block: HttpRequestRetry.Configuration.() -> Unit) =
    install(HttpRequestRetry, block)