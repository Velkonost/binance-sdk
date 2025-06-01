package velkonost.binance.sdk.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import velkonost.binance.sdk.data.repository.FuturesRepository
import velkonost.binance.sdk.data.repository.MainRepository
import velkonost.binance.sdk.data.repository.SocketRepository
import velkonost.binance.sdk.data.repository.Symbol
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.model.*

/**
 * The core implementation class of the Binance SDK.
 * This class handles all direct interactions with the Binance API, including REST and WebSocket operations.
 * It manages authentication, request signing, and response handling for both spot and futures trading.
 *
 * @property mainRepository Repository for general Binance API operations
 * @property futuresRepository Repository for futures-specific API operations
 * @property socketRepository Repository for WebSocket operations and real-time data streams
 */
internal class Client(
    apiKey: String, apiSecret: String,
    private val mainRepository: MainRepository = MainRepository(apiKey, apiSecret),
    private val futuresRepository: FuturesRepository = FuturesRepository(apiKey, apiSecret),
    private val socketRepository: SocketRepository = SocketRepository(apiKey, apiSecret)
) {

    /**
     * Coroutine scope for managing background tasks within the client.
     * Uses [Dispatchers.Default] for optimal performance across all supported platforms.
     */
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // SOCKET OPERATIONS

    /**
     * Subscribes to WebSocket updates for all available trading symbols.
     * This method manages multiple WebSocket connections efficiently with configurable delays.
     *
     * @param delayBetweenLaunches The delay (in milliseconds) between starting subscriptions for each symbol
     * @param interval The candlestick interval for which updates are requested
     * @param logConnectionsState If true, logs the state of WebSocket connections for monitoring
     * @param collector An optional callback function to process incoming symbol updates
     * @return A list of flows, each representing a stream of updates for a specific symbol
     */
    internal suspend fun socketListenAllSymbols(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        collector: ((SymbolUpdate) -> Unit)? = null
    ): List<Flow<SymbolUpdate>> {
        val result = CompletableDeferred<List<Flow<SymbolUpdate>>>()
        val sockets = mutableListOf<Flow<SymbolUpdate>>()
        val symbols = futuresExchangeSymbols()

        if (logConnectionsState) {
            socketListenConnections()
        }

        coroutineScope.launch {
            symbols.forEach { symbol ->
                delay(delayBetweenLaunches)

                val newSocket = async { socketListenUpdates(symbol, interval) }.await()
                collector?.let { launch { newSocket.collect(it) } }

                sockets.add(newSocket)
            }
            result.complete(sockets)
        }
        return result.await()
    }

    /**
     * Starts monitoring the state of all WebSocket connections.
     * This is useful for debugging and ensuring connection health.
     */
    internal fun socketListenConnections() = coroutineScope.launch { socketRepository.listenSocketsConnections() }

    /**
     * Subscribes to WebSocket updates for a specific trading symbol.
     *
     * @param symbol The trading symbol to subscribe to (e.g., "BTCUSDT")
     * @param interval The candlestick interval for which updates are requested
     * @return A flow of [SymbolUpdate] objects containing real-time market data
     */
    internal suspend fun socketListenUpdates(symbol: Symbol, interval: KlineInterval) =
        socketRepository.getSocketIncoming(symbol, interval).map { it.toSymbolUpdate() }

    /**
     * Stops listening to WebSocket updates for a specific trading symbol.
     * This will close the WebSocket connection for the specified symbol.
     *
     * @param symbol The trading symbol to unsubscribe from
     */
    internal suspend fun socketStopListenUpdates(symbol: Symbol) = socketRepository.closeSocket(symbol)

    // GENERAL API OPERATIONS

    /**
     * Pings the Binance API to check connectivity.
     * This is a simple health-check method that can be used to verify API access.
     */
    internal suspend fun ping() = mainRepository.ping()

    // FUTURES API OPERATIONS

    /**
     * Pings the Binance Futures API to check connectivity.
     * Similar to [ping], but specifically for the futures endpoint.
     */
    internal suspend fun futuresPing() = futuresRepository.ping()

    /**
     * Retrieves the balance for a specific asset in the futures account.
     *
     * @param asset The asset for which the balance is requested (e.g., "USDT")
     * @return A [FuturesBalance] object containing the balance information
     */
    internal suspend fun futuresGetBalance(asset: String): FuturesBalance {
        val result = futuresRepository.getBalance(asset)
        return result.toFuturesBalance()
    }

    /**
     * Retrieves all open positions in the futures account.
     *
     * @return A list of [FuturesOpenPosition] objects containing position details
     */
    internal suspend fun futuresOpenPosition(): List<FuturesOpenPosition> {
        val result = futuresRepository.getOpenPositions()
        return result.toFuturesOpenPositions()
    }

    /**
     * Retrieves all available trading symbols for futures trading.
     *
     * @return A list of trading symbols (e.g., "BTCUSDT", "ETHUSDT")
     */
    internal suspend fun futuresExchangeSymbols(): List<Symbol> {
        val result = futuresRepository.getExchangeSymbols()
        return result.map { it.symbol }
    }

    /**
     * Retrieves historical kline (candlestick) data for a specific symbol.
     *
     * @param symbol The trading symbol (e.g., "ETHUSDT")
     * @param interval The candlestick interval (e.g., [KlineInterval.Minute1])
     * @param start The start time for historical data (e.g., "4 day ago")
     * @return A list of [Kline] objects containing the candlestick data
     */
    internal suspend fun futuresHistoricalKlines(symbol: Symbol, interval: KlineInterval, start: String): List<Kline> {
        val result = futuresRepository.getHistoricalKlines(symbol, interval, start)
        return result.toKlines()
    }
}