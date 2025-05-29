package velkonost.binance.sdk.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

internal actual val platformEngine: HttpClientEngineFactory<*>
    get() = OkHttp