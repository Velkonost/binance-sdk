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

internal class FuturesDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {

    override val httpClient: HttpClient by inject {
        parametersOf(Urls.Futures.value, getHeaders())
    }

    internal suspend fun ping(): ResultState<Unit> = httpClient.get(FuturesRoute.Ping.path).mapToState()

    internal suspend fun getBalance(): ResultState<List<BalanceResponse>> =
        httpClient.binanceGet(FuturesRoute.Balance.path).mapToState()


    internal suspend fun getPositions(): ResultState<List<SymbolPositionResponse>> =
        httpClient.binanceGet(FuturesRoute.Positions.path).mapToState()


    internal suspend fun getExchangeInfo(): ResultState<ExchangeInfoResponse> =
        httpClient.binanceGet(FuturesRoute.ExchangeInfo.path).mapToState()


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

    internal suspend fun getListenKey(): ResultState<ListenKeyResponse> {
        val response = httpClient.post(FuturesRoute.ListenKey.path)
        return response.mapToState()
    }

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

private enum class FuturesRoute(val path: String) {
    Ping("${ApiVersion.FuturesV1.value}/ping"),
    Balance("${ApiVersion.FuturesV2.value}/balance"),
    Positions("${ApiVersion.FuturesV2.value}/positionRisk"),
    ExchangeInfo("${ApiVersion.FuturesV1.value}/exchangeInfo"),
    Klines("${ApiVersion.FuturesV1.value}/klines"),
    ListenKey("${ApiVersion.FuturesV1.value}/listenKey"),
}