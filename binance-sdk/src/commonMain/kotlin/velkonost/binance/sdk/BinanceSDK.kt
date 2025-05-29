package velkonost.binance.sdk

import org.koin.core.context.loadKoinModules
import velkonost.binance.sdk.client.Client
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.BinanceSDKNotInitializedException
import velkonost.binance.sdk.model.SymbolUpdate
import velkonost.binance.sdk.network.di.BinanceSDKNetworkModule

object BinanceSDK {

    private var client: Client? = null

    fun setup(apiKey: String, apiSecret: String) {
        initDI()
        client = Client(apiKey, apiSecret)
    }

    private fun initDI() = loadKoinModules(BinanceSDKNetworkModule)

    private suspend fun <T> safeCall(function: suspend (Client) -> T) =
        client?.let { function(it) } ?: throw BinanceSDKNotInitializedException

    object General {
        suspend fun ping() = safeCall { it.ping() }
    }

    object Futures {
        suspend fun ping() = safeCall { it.futuresPing() }
        suspend fun getBalance(asset: String = "USDT") = safeCall { it.futuresGetBalance(asset) }
        suspend fun getOpenPositions() = safeCall { it.futuresOpenPosition() }
        suspend fun getSymbols() = safeCall { it.futuresExchangeSymbols() }
        suspend fun getKlines(
            symbol: String = "ETHUSDT",
            interval: KlineInterval = KlineInterval.Minute1,
            start: String = "4 day ago"
        ) = safeCall { it.futuresHistoricalKlines(symbol, interval, start) }
    }

    object Socket {
        suspend fun startListenConnections() = safeCall { it.socketListenConnections() }
        suspend fun startListenAllSymbolsUpdates(
            delayBetweenLaunches: Long = 1000L,
            interval: KlineInterval = KlineInterval.Minute1,
            logConnectionsState: Boolean = true,
            collector: ((SymbolUpdate) -> Unit)? = null
        ) = safeCall {
            it.socketListenAllSymbols(delayBetweenLaunches, interval, logConnectionsState, collector)
        }

        suspend fun startListenUpdates(symbol: String, interval: KlineInterval = KlineInterval.Minute1) =
            safeCall { it.socketListenUpdates(symbol, interval) }

        suspend fun stopListenUpdates(symbol: String) = safeCall { it.socketStopListenUpdates(symbol) }
    }

}