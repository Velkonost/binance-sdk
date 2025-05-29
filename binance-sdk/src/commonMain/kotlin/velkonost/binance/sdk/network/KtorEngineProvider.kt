package velkonost.binance.sdk.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.*

internal expect val platformEngine: HttpClientEngineFactory<*>
internal fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(platformEngine) {
        config(this)
    }