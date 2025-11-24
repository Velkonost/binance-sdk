package velkonost.binance.sdk.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*

internal actual fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(CIO) {
        config(this)
    }
