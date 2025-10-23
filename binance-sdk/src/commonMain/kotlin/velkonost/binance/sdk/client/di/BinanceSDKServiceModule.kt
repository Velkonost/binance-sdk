package velkonost.binance.sdk.client.di

import org.koin.dsl.module
import velkonost.binance.sdk.client.*
import velkonost.binance.sdk.client.internal.*

internal val BinanceSDKServiceModule = module {
    single<AccountService> { AccountServiceImpl(repository = get()) }
    single<FuturesService> { FuturesServiceImpl(repository = get()) }
    single<GeneralService> { GeneralServiceImpl(repository = get()) }
    single<SocketService> { SocketServiceImpl(repository = get(), futuresRepository = get()) }
    single<SpotService> { SpotServiceImpl(repository = get()) }
    single { Client() }
}