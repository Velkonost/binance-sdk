package velkonost.binance.sdk

import org.koin.core.context.loadKoinModules
import velkonost.binance.sdk.client.Client
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.BinanceSDKNotInitializedException
import velkonost.binance.sdk.model.SymbolUpdate
import velkonost.binance.sdk.network.di.BinanceSDKNetworkModule

/**
 * The main entry point for the Binance SDK.
 * This object provides a simplified interface for interacting with the Binance API,
 * supporting both REST and WebSocket operations for spot and futures trading.
 *
 * Before using any functionality, the SDK must be initialized using [setup] with valid API credentials.
 */
object BinanceSDK {

    private var client: Client? = null

    /**
     * Initializes the Binance SDK with API credentials.
     * This method must be called before using any other SDK functionality.
     *
     * @param apiKey The API key provided by Binance for authentication
     * @param apiSecret The secret key used for signing requests to the Binance API
     */
    fun setup(apiKey: String, apiSecret: String) {
        initDI()
        client = Client(apiKey, apiSecret)
    }

    private fun initDI() = loadKoinModules(BinanceSDKNetworkModule)

    /**
     * Executes a function safely with the initialized client.
     * Throws [BinanceSDKNotInitializedException] if the SDK hasn't been initialized.
     */
    private suspend fun <T> safeCall(function: suspend (Client) -> T) =
        client?.let { function(it) } ?: throw BinanceSDKNotInitializedException

    /**
     * General API operations that are not specific to any trading type.
     */
    object General {
        /**
         * Pings the Binance API to check connectivity.
         * This is a simple health-check method that can be used to verify API access.
         */
        suspend fun ping() = safeCall { it.ping() }
    }

    /**
     * Futures trading specific operations.
     * These methods interact with the Binance Futures API endpoints.
     */
    object Futures {
        /**
         * Pings the Binance Futures API to check connectivity.
         * Similar to [General.ping], but specifically for the futures endpoint.
         */
        suspend fun ping() = safeCall { it.futuresPing() }

        /**
         * Retrieves the balance for a specific asset in the futures account.
         *
         * @param asset The asset for which the balance is requested (default: "USDT")
         * @return A [FuturesBalance] object containing the balance information
         */
        suspend fun getBalance(asset: String = "USDT") = safeCall { it.futuresGetBalance(asset) }

        /**
         * Retrieves all open positions in the futures account.
         *
         * @return A list of [FuturesOpenPosition] objects containing position details
         */
        suspend fun getOpenPositions() = safeCall { it.futuresOpenPosition() }

        /**
         * Retrieves all available trading symbols for futures trading.
         *
         * @return A list of trading symbols (e.g., "BTCUSDT", "ETHUSDT")
         */
        suspend fun getSymbols() = safeCall { it.futuresExchangeSymbols() }

        /**
         * Retrieves historical kline (candlestick) data for a specific symbol.
         *
         * @param symbol The trading symbol (default: "ETHUSDT")
         * @param interval The candlestick interval (default: [KlineInterval.Minute1])
         * @param start The start time for historical data (default: "4 day ago")
         * @return A list of [Kline] objects containing the candlestick data
         */
        suspend fun getKlines(
            symbol: String = "ETHUSDT",
            interval: KlineInterval = KlineInterval.Minute1,
            start: String = "4 day ago"
        ) = safeCall { it.futuresHistoricalKlines(symbol, interval, start) }
    }

    /**
     * WebSocket operations for real-time market data.
     * These methods provide access to live market data streams.
     */
    object Socket {
        /**
         * Starts listening to the state of WebSocket connections.
         * This is useful for monitoring connection health and debugging.
         */
        suspend fun startListenConnections() = safeCall { it.socketListenConnections() }

        /**
         * Starts listening to real-time updates for all available trading symbols.
         *
         * @param delayBetweenLaunches The delay (in milliseconds) between starting subscriptions for each symbol
         * @param interval The candlestick interval for which updates are requested
         * @param logConnectionsState If true, logs the state of WebSocket connections
         * @param collector An optional callback function to process incoming symbol updates
         */
        suspend fun startListenAllSymbolsUpdates(
            delayBetweenLaunches: Long = 1000L,
            interval: KlineInterval = KlineInterval.Minute1,
            logConnectionsState: Boolean = true,
            collector: ((SymbolUpdate) -> Unit)? = null
        ) = safeCall {
            it.socketListenAllSymbols(delayBetweenLaunches, interval, logConnectionsState, collector)
        }

        /**
         * Starts listening to real-time updates for a specific trading symbol.
         *
         * @param symbol The trading symbol to subscribe to
         * @param interval The candlestick interval for which updates are requested
         */
        suspend fun startListenUpdates(symbol: String, interval: KlineInterval = KlineInterval.Minute1) =
            safeCall { it.socketListenUpdates(symbol, interval) }

        /**
         * Stops listening to real-time updates for a specific trading symbol.
         *
         * @param symbol The trading symbol to unsubscribe from
         */
        suspend fun stopListenUpdates(symbol: String) = safeCall { it.socketStopListenUpdates(symbol) }
    }

}