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

    private fun <T> safeCall(function: (Client) -> T) = client?.let(function) ?: throw BinanceSDKNotInitializedException

    object General {
        fun ping() = safeCall { it.ping() }
    }

    object Futures {
        fun ping() = safeCall { it.futuresPing() }
        fun getBalance(asset: String = "USDT") = safeCall { it.futuresGetBalance(asset) }
        fun getOpenPositions() = safeCall { it.futuresOpenPosition() }
        fun getSymbols() = safeCall { it.futuresExchangeSymbols() }
        fun getKlines(
            symbol: String = "ETHdsfUSDT",
            interval: KlineInterval = KlineInterval.Minute1,
            start: String = "4 day ago"
        ) = safeCall { it.futuresHistoricalKlines(symbol, interval, start) }
    }

    object Socket {
        fun startListenConnections() = safeCall { it.socketListenConnections() }
        fun startListenAllSymbolsUpdates(
            delayBetweenLaunches: Long = 1000L,
            interval: KlineInterval = KlineInterval.Minute1,
            logConnectionsState: Boolean = true,
            collector: ((SymbolUpdate) -> Unit)? = null
        ) = safeCall {
            it.socketListenAllSymbols(delayBetweenLaunches, interval, logConnectionsState, collector)
        }

        fun startListenUpdates(symbol: String, interval: KlineInterval = KlineInterval.Minute1) =
            safeCall { it.socketListenUpdates(symbol, interval) }

        fun stopListenUpdates(symbol: String) = safeCall { it.socketStopListenUpdates(symbol) }
    }

}