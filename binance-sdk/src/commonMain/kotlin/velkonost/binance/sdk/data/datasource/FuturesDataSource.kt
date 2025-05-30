package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import velkonost.binance.sdk.data.model.response.*
import velkonost.binance.sdk.enums.*
import velkonost.binance.sdk.network.ResultState
import velkonost.binance.sdk.network.extensions.get
import velkonost.binance.sdk.network.extensions.post
import velkonost.binance.sdk.network.socket.WSSession
import velkonost.binance.sdk.network.socket.wsSession

/**
 * Data source class for making requests to Binance Futures API endpoints.
 * This class handles the low-level HTTP communication with the Binance Futures API,
 * including request signing and response parsing.
 *
 * @property httpClient The HTTP client configured for Binance Futures API communication
 */
internal class FuturesDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {

    override val httpClient: HttpClient by inject {
        parametersOf(Urls.Futures.value, getHeaders())
    }

    /**
     * Pings the Binance Futures API to check connectivity.
     *
     * @return A [ResultState] indicating the success or failure of the ping request
     */
    internal suspend fun ping(): ResultState<Unit> = httpClient.get(FuturesRoute.Ping.path).mapToState()

    /**
     * Retrieves the balance information for all assets in the futures account.
     *
     * @return A [ResultState] containing a list of [BalanceResponse] objects
     */
    internal suspend fun getBalance(): ResultState<List<BalanceResponse>> =
        httpClient.binanceGet(FuturesRoute.Balance.path).mapToState()

    /**
     * Retrieves position information for all symbols in the futures account.
     *
     * @return A [ResultState] containing a list of [SymbolPositionResponse] objects
     */
    internal suspend fun getPositions(): ResultState<List<SymbolPositionResponse>> =
        httpClient.binanceGet(FuturesRoute.Positions.path).mapToState()

    /**
     * Retrieves exchange information including available trading symbols and their specifications.
     *
     * @return A [ResultState] containing an [ExchangeInfoResponse] object
     */
    internal suspend fun getExchangeInfo(): ResultState<ExchangeInfoResponse> =
        httpClient.binanceGet(FuturesRoute.ExchangeInfo.path).mapToState()

    /**
     * Retrieves historical kline (candlestick) data for a specific symbol.
     *
     * @param symbol The trading symbol (e.g., "BTCUSDT")
     * @param interval The candlestick interval (e.g., [KlineInterval.Minute1])
     * @param limit The maximum number of candlesticks to retrieve
     * @param startTime The start time for historical data in milliseconds
     * @param endTime Optional end time for historical data in milliseconds
     * @return A [ResultState] containing a list of candlestick data points
     */
    internal suspend fun getKlines(
        symbol: String,
        interval: KlineInterval,
        limit: Int,
        startTime: Long,
        endTime: Long? = null
    ): ResultState<List<List<Float>>> {
        val parameters = mutableListOf(
            "symbol" to symbol,
            "interval" to interval.value,
            "limit" to limit.toString(),
            "startTime" to startTime.toString(),
            "endTime" to endTime.toString(),
            "type" to KlineType.Futures.value.toString()
        )
        endTime?.let { parameters += "endTime" to endTime.toString() }

        val response = httpClient.binanceGet(
            path = FuturesRoute.Klines.path,
            parameters = parameters
        )
        return response.mapToState<List<List<Float>>>()
    }

    /**
     * Retrieves a listen key for maintaining a user data stream.
     * This key is required for WebSocket connections that receive user-specific updates.
     *
     * @return A [ResultState] containing a [ListenKeyResponse] object
     */
    internal suspend fun getListenKey(): ResultState<ListenKeyResponse> {
        val response = httpClient.post(FuturesRoute.ListenKey.path)
        return response.mapToState()
    }

    /**
     * Launches a WebSocket connection for real-time market data.
     *
     * @param symbol The trading symbol to subscribe to (default: "ETHUSDT")
     * @param interval The candlestick interval for updates (default: [KlineInterval.Minute1])
     * @return A [WSSession] object for managing the WebSocket connection
     */
    internal fun launchWebSocket(
        symbol: String = "ETHUSDT",
        interval: KlineInterval = KlineInterval.Minute1
    ): WSSession<SocketResponse> {
        val session = httpClient.wsSession<SocketResponse>(
            url = "${SocketUrls.FStream.value}${symbol.lowercase()}_${ContractType.Perpetual.value}@continuousKline_${interval.value}",
        )
        return session
    }
}

/**
 * Enum class defining the API endpoints for Binance Futures operations.
 */
private enum class FuturesRoute(val path: String) {
    Ping("${ApiVersion.FuturesV1.value}/ping"),
    Balance("${ApiVersion.FuturesV2.value}/balance"),
    Positions("${ApiVersion.FuturesV2.value}/positionRisk"),
    ExchangeInfo("${ApiVersion.FuturesV1.value}/exchangeInfo"),
    Klines("${ApiVersion.FuturesV1.value}/klines"),
    ListenKey("${ApiVersion.FuturesV1.value}/listenKey")
}