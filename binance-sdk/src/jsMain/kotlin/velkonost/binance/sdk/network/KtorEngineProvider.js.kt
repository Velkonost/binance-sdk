package velkonost.binance.sdk.network

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

internal actual val platformEngine: HttpClientEngineFactory<*>
    get() = Js