package velkonost.binance.sdk.network

import io.ktor.client.*
import io.ktor.client.engine.js.*

internal actual fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Js) {
        config(this)
    }
