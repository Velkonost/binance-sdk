package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import velkonost.binance.sdk.data.model.response.*
import velkonost.binance.sdk.enums.*
import velkonost.binance.sdk.network.ResultState
import velkonost.binance.sdk.network.extensions.get
import velkonost.binance.sdk.network.extensions.post
import velkonost.binance.sdk.network.socket.WSSession
import velkonost.binance.sdk.network.socket.wsSession

internal class FuturesDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {

    override val httpClient: HttpClient by inject(HttpClient::class.java) {
        parametersOf(Urls.Futures.value, getHeaders())
    }

    internal suspend fun ping(): ResultState<Unit> = withContext(Dispatchers.IO) {
        httpClient.get(FuturesRoute.Ping.path).mapToState()
    }

    internal suspend fun getBalance(): ResultState<List<BalanceResponse>> = withContext(Dispatchers.IO) {
        httpClient.binanceGet(FuturesRoute.Balance.path).mapToState()
    }

    internal suspend fun getPositions(): ResultState<List<SymbolPositionResponse>> = withContext(Dispatchers.IO) {
        httpClient.binanceGet(FuturesRoute.Positions.path).mapToState()
    }

    internal suspend fun getExchangeInfo(): ResultState<ExchangeInfoResponse> = withContext(Dispatchers.IO) {
        httpClient.binanceGet(FuturesRoute.ExchangeInfo.path).mapToState()
    }

    internal suspend fun getKlines(
        symbol: String,
        interval: KlineInterval,
        limit: Int,
        startTime: Long,
        endTime: Long? = null
    ): ResultState<List<List<Float>>> = withContext(Dispatchers.IO) {
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
        response.mapToState<List<List<Float>>>()
    }

    internal suspend fun getListenKey(): ResultState<ListenKeyResponse> = withContext(Dispatchers.IO) {
        val response = httpClient.post(FuturesRoute.ListenKey.path)
        response.mapToState()
    }

    internal fun launchWebSocket(
        symbol: String = "ETHUSDT",
        interval: KlineInterval = KlineInterval.Minute1
    ): WSSession<SocketResponse> {
        val session = httpClient.wsSession<SocketResponse>(
            url = "${SocketUrls.FStream.value}${symbol.lowercase()}_${ContractType.Perpetual.value}@continuousKline_${interval.value}",
        )
        println("Socket $symbol launch")
        return session
    }
}

private enum class FuturesRoute(val path: String) {
    Ping("${ApiVersion.FuturesV1.value}/ping"),
    Balance("${ApiVersion.FuturesV2.value}/balance"),
    Positions("${ApiVersion.FuturesV2.value}/positionRisk"),
    ExchangeInfo("${ApiVersion.FuturesV1.value}/exchangeInfo"),
    Klines("${ApiVersion.FuturesV1.value}/klines"),
    ListenKey("${ApiVersion.FuturesV1.value}/listenKey"),
}