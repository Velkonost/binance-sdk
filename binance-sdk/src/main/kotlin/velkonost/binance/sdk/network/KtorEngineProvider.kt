package velkonost.binance.sdk.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.*

internal fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(CIO) {
        config(this)
        engine {
            endpoint {
                connectAttempts = 5
            }

            this.requestTimeout = KTOR_REQUEST_TIMEOUT_MILLIS
        }
    }