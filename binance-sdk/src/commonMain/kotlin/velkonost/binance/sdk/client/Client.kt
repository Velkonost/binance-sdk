package velkonost.binance.sdk.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import velkonost.binance.sdk.data.repository.*
import velkonost.binance.sdk.data.repository.FuturesRepository
import velkonost.binance.sdk.data.repository.MainRepository
import velkonost.binance.sdk.data.repository.SocketRepository
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
    private val spotRepository: SpotRepository = SpotRepository(apiKey, apiSecret),
    private val futuresRepository: FuturesRepository = FuturesRepository(apiKey, apiSecret),
    private val socketRepository: SocketRepository = SocketRepository(apiKey, apiSecret),
    private val accountRepository: AccountRepository = AccountRepository(apiKey, apiSecret),
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
     * Optimized version that returns a single unified Flow<SymbolUpdate> with parallel processing
     * and guaranteed no loss of elements.
     *
     * @param delayBetweenLaunches The delay (in milliseconds) between starting subscriptions for each symbol
     * @param interval The candlestick interval for which updates are requested
     * @param logConnectionsState If true, logs the state of WebSocket connections for monitoring
     * @param maxConcurrency Maximum number of concurrent symbol subscriptions (default: 20)
     * @return A single unified flow that emits updates from all symbols with parallel processing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    internal suspend fun socketListenAllSymbols(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        maxConcurrency: Int = 20
    ): Flow<SymbolUpdate> {
        if (logConnectionsState) {
            socketListenConnections()
        }

        val symbols = try {
            futuresExchangeSymbols()
        } catch (e: Exception) {
            return flowOf()
        }

        val symbolsFlow = flow {
            symbols.forEachIndexed { index, symbol ->
                if (index > 0) {
                    delay(delayBetweenLaunches)
                }
                emit(symbol)
            }
        }

        return symbolsFlow
            .flatMapMerge(concurrency = maxConcurrency) { symbol ->
                try {
                    socketListenUpdates(symbol, interval)
                        .catch { e ->
                            println("Error in socket for symbol $symbol: ${e.message}")
                        }
                } catch (e: Exception) {
                    println("Failed to create socket for symbol $symbol: ${e.message}")
                    flowOf()
                }
            }
            .flowOn(Dispatchers.Default)
    }

    /**
     * Advanced version of socketListenAllSymbols with additional performance optimizations.
     * This method provides fine-grained control over buffering and conflation for high-throughput scenarios.
     *
     * @param delayBetweenLaunches The delay (in milliseconds) between starting subscriptions for each symbol
     * @param interval The candlestick interval for which updates are requested
     * @param logConnectionsState If true, logs the state of WebSocket connections for monitoring
     * @param maxConcurrency Maximum number of concurrent symbol subscriptions (default: 20)
     * @param bufferSize Buffer size for each symbol's flow (default: 64)
     * @param conflateUpdates If true, conflates updates to prevent overwhelming downstream consumers
     * @return A single unified flow with advanced performance optimizations
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    internal suspend fun socketListenAllSymbolsAdvanced(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        maxConcurrency: Int = 20,
        bufferSize: Int = 64,
        conflateUpdates: Boolean = false
    ): Flow<SymbolUpdate> {
        if (logConnectionsState) {
            socketListenConnections()
        }

        val symbols = try {
            futuresExchangeSymbols()
        } catch (e: Exception) {
            return flowOf()
        }

        val symbolsFlow = flow {
            symbols.forEachIndexed { index, symbol ->
                if (index > 0) {
                    delay(delayBetweenLaunches)
                }
                emit(symbol)
            }
        }

        return symbolsFlow
            .flatMapMerge(concurrency = maxConcurrency) { symbol ->
                try {
                    val symbolFlow = socketListenUpdates(symbol, interval)
                        .catch { e ->
                            println("Error in socket for symbol $symbol: ${e.message}")
                        }

                    when {
                        conflateUpdates -> symbolFlow.conflate()
                        else -> symbolFlow.buffer(bufferSize)
                    }
                } catch (e: Exception) {
                    println("Failed to create socket for symbol $symbol: ${e.message}")
                    flowOf()
                }
            }
            .flowOn(Dispatchers.Default)
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

    internal suspend fun getDepositHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ) = accountRepository.getDeposit(asset, startTime, endTime)

    internal suspend fun getWithdrawHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ) = accountRepository.getWithdraw(asset, startTime, endTime)

    // SPOT API OPERATIONS

    internal suspend fun spotGetAllBalance(asset: String? = null) = spotRepository.getBalance(asset)

    internal suspend fun spotGetTrades(
        asset: String,
        startTime: Long? = null,
        endTime: Long? = null
    ) = spotRepository.getTrades(asset, startTime, endTime)

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