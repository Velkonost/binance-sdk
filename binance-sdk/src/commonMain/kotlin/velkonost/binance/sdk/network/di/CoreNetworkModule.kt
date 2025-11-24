package velkonost.binance.sdk.network.di

import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import org.koin.dsl.module
import velkonost.binance.sdk.network.ktorClient

internal fun BinanceSDKNetworkModule(logLevel: LogLevel) = module {
    factory<HttpClient> { params -> ktorClient(
        url = params.get(),
        headers = params.get(),
        logLevel = logLevel
    ) }
}
