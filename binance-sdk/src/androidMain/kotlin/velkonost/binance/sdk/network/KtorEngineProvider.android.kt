package velkonost.binance.sdk.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

internal actual fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp) {
        config(this)
    }
