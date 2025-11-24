package velkonost.binance.sdk.network

import io.ktor.client.*
import io.ktor.client.engine.darwin.*

internal actual fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Darwin) {
        config(this)
    }
