package velkonost.binance.sdk.data.di

import org.koin.dsl.module
import velkonost.binance.sdk.data.repository.*

internal fun BinanceSDKDataModule(apiKey: String, apiSecret: String) = module {
    single { AccountRepository(apiKey, apiSecret) }
    single { FuturesRepository(apiKey, apiSecret) }
    single { MainRepository(apiKey, apiSecret) }
    single { SocketRepository(apiKey, apiSecret) }
    single { SpotRepository(apiKey, apiSecret) }
}