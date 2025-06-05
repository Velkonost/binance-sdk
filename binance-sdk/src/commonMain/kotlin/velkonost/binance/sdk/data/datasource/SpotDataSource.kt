package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import velkonost.binance.sdk.data.model.response.AccountResponse
import velkonost.binance.sdk.data.model.response.BalanceResponse
import velkonost.binance.sdk.data.model.response.SpotTradesResponse
import velkonost.binance.sdk.enums.ApiVersion
import velkonost.binance.sdk.enums.Urls
import velkonost.binance.sdk.network.ResultState

internal class SpotDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {
    override val httpClient: HttpClient by inject {
        parametersOf(Urls.Api.value, getHeaders())
    }

    internal suspend fun getAccount(): ResultState<AccountResponse> =
        httpClient.binanceGet(SpotRoute.Account.path, withTimeout = false).mapToState()

    internal suspend fun getTrades(
        symbol: String,
        startTime: Long? = null,
        endTime: Long? = null
    ): ResultState<List<SpotTradesResponse>> {
        val parameters = mutableListOf(
            "symbol" to symbol,
            "limit" to 1000.toString()
        )
        startTime?.let { parameters += "startTime" to startTime.toString() }
        endTime?.let { parameters += "endTime" to endTime.toString() }

        return httpClient.binanceGet(
            path = SpotRoute.Trades.path,
            withTimeout = false,
            parameters = parameters
        ).mapToState()
    }

}

private enum class SpotRoute(val path: String) {
    Account("${ApiVersion.Private.value}/account"),
    Trades("${ApiVersion.Private.value}/myTrades"),
    Deposit("${ApiVersion.Private.value}/myTrades"),
}