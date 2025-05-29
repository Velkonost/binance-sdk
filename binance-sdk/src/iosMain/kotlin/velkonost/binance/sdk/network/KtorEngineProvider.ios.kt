package velkonost.binance.sdk.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

internal actual val platformEngine: HttpClientEngineFactory<*>
    get() = Darwin