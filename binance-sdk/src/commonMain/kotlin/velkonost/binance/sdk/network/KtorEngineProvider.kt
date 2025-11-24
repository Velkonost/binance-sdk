package velkonost.binance.sdk.network

import io.ktor.client.*

internal expect fun withPlatformEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient
