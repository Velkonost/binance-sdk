package velkonost.binance.sdk.network.di

import io.ktor.client.*
import org.koin.dsl.module
import velkonost.binance.sdk.network.ktorClient

val BinanceSDKNetworkModule = module {
    factory<HttpClient> { params -> ktorClient(params.get(), params.get()) }
}