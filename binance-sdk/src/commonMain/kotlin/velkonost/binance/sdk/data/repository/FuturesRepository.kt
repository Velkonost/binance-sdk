package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import velkonost.binance.sdk.data.datasource.FuturesDataSource
import velkonost.binance.sdk.data.model.response.*
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.extensions.convertIntervalToMills
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.extensions.toMilliseconds
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess
import kotlin.math.max

/**
 * Repository class for handling Binance Futures API operations.
 * This class provides methods for interacting with the Binance Futures API endpoints,
 * including balance queries, position management, and market data retrieval.
 *
 * @property dataSource The data source for making API requests to Binance Futures endpoints
 */
internal class FuturesRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: FuturesDataSource = FuturesDataSource(apiKey, apiSecret)
) : Repository() {

    /**
     * Pings the Binance Futures API to check connectivity.
     * This is a simple health-check method that can be used to verify API access.
     *
     * @return true if the ping was successful, false otherwise
     */
    suspend fun ping(): Boolean {
        val result = CompletableDeferred<Boolean>()
        coroutineScope.launchCatching(catch = { result.complete(false) }) {
            dataSource.ping()
                .onSuccess { result.complete(true) }
                .onFailure { _, _ -> result.complete(false) }
        }
        return result.await()
    }

    /**
     * Retrieves the balance for a specific asset in the futures account.
     *
     * @param asset The asset for which the balance is requested (e.g., "USDT")
     * @return A [BalanceResponse] object containing the balance information
     * @throws BinanceSDKException if the balance for the specified asset is not found
     */
    suspend fun getBalance(asset: String): BalanceResponse {
        val result = CompletableDeferred<BalanceResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getBalance()
                .onSuccess { list ->
                    list.firstOrNull { it.asset == asset }?.let { result.complete(it) }
                        ?: result.handleError(BinanceSDKException("balance in $asset not found"))
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

    /**
     * Retrieves all open positions in the futures account.
     * Only positions with non-zero notional value are included.
     *
     * @return A list of [SymbolPositionResponse] objects containing position details
     */
    suspend fun getOpenPositions(): List<SymbolPositionResponse> {
        val result = CompletableDeferred<List<SymbolPositionResponse>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getPositions()
                .onSuccess { list ->
                    result.complete(list.filter { it.notional != 0.0f })
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

    /**
     * Retrieves all available trading symbols for futures trading.
     * Only active trading symbols are returned (status = "TRADING").
     *
     * @return A list of [ExchangeSymbolData] objects containing symbol information
     */
    suspend fun getExchangeSymbols(): List<ExchangeSymbolData> {
        val result = CompletableDeferred<List<ExchangeSymbolData>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getExchangeInfo()
                .onSuccess { data ->
                    val eligibleSymbols = data.symbols.filter { "_" !in it.symbol && it.status == "TRADING" }
                    result.complete(eligibleSymbols)
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

    /**
     * Retrieves historical kline (candlestick) data for a specific symbol.
     *
     * @param symbol The trading symbol (e.g., "BTCUSDT")
     * @param interval The candlestick interval (e.g., [KlineInterval.Minute1])
     * @param start The start time for historical data
     * @param end Optional end time for historical data
     * @param limit The maximum number of candlesticks to retrieve (default: 1000)
     * @return A list of [KlineResponse] objects containing the candlestick data
     */
    suspend fun getHistoricalKlines(
        symbol: String,
        interval: KlineInterval,
        start: String?,
        end: String? = null,
        limit: Int = 1000
    ): List<KlineResponse> {
        val result = CompletableDeferred<List<KlineResponse>>()
        val resultList = mutableListOf<KlineResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            val timeframe = interval.value.convertIntervalToMills()

            var convertedStartTime = start?.toMilliseconds() ?: Clock.System.now().toEpochMilliseconds()
            val firstKline = getEarliestValidKline(symbol, interval)
            val firstValidStartTime = firstKline.openTime.toLong()
            firstValidStartTime.let {
                convertedStartTime = max(convertedStartTime, firstValidStartTime)
            }

            val convertedEndTime = end?.toMilliseconds() ?: Clock.System.now().toEpochMilliseconds()
            if (convertedEndTime <= convertedStartTime) {
                result.complete(emptyList())
                return@launchCatching
            }

            var idx = 0
            var shouldBreak = false
            while (!shouldBreak) {
                dataSource.getKlines(
                    symbol = symbol,
                    interval = interval,
                    limit = limit,
                    startTime = convertedStartTime,
                    endTime = convertedEndTime
                ).onSuccess { list ->
                    val klines = list.map { it.mapToKline() }
                    resultList.addAll(klines)
                    convertedStartTime = klines.last().openTime.toLong() + timeframe

                    if (list.isEmpty() || list.size < limit || convertedStartTime >= convertedEndTime) {
                        shouldBreak = true
                    }
                }.onFailure { _, _ -> shouldBreak = true }

                idx++
                if (idx % 3 == 0) delay(3000)
            }
            result.complete(resultList)

        }

        return result.await()
    }

    private suspend fun getEarliestValidKline(
        symbol: String,
        interval: KlineInterval,
    ): KlineResponse {
        val result = CompletableDeferred<KlineResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getKlines(
                symbol = symbol,
                interval = interval,
                limit = 1,
                startTime = 0,
                endTime = Clock.System.now().toEpochMilliseconds()
            ).onSuccess { list ->
                list.firstOrNull()?.let { klineData ->
                    val kline = klineData.mapToKline()
                    result.complete(kline)
                } ?: result.handleError(
                    BinanceSDKException("error find first valid kline for $symbol with interval ${interval.value}")
                )
            }.onFailure(result::handleError)
        }
        return result.await()
    }

    /**
     * Retrieves a listen key for maintaining a user data stream.
     * This key is required for WebSocket connections that receive user-specific updates.
     *
     * @return A listen key string that can be used to establish a user data stream
     */
    suspend fun getListenKey(): String {
        val result = CompletableDeferred<String>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getListenKey()
                .onSuccess { result.complete(it.listenKey) }
                .onFailure(result::handleError)
        }
        return result.await()
    }

}