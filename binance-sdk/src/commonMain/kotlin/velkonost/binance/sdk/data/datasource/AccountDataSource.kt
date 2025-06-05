package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import velkonost.binance.sdk.data.model.response.DepositResponse
import velkonost.binance.sdk.data.model.response.WithdrawResponse
import velkonost.binance.sdk.enums.ApiVersion
import velkonost.binance.sdk.enums.Urls
import velkonost.binance.sdk.network.ResultState

internal class AccountDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {
    override val httpClient: HttpClient by inject {
        parametersOf(Urls.MarginApi.value, getHeaders())
    }

    internal suspend fun getDepositHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): ResultState<List<DepositResponse>> {
        val parameters = mutableListOf(
            "limit" to 1000.toString()
        )
        asset?.let { parameters += "coin" to asset }
        startTime?.let { parameters += "startTime" to startTime.toString() }
        endTime?.let { parameters += "endTime" to endTime.toString() }

        return httpClient.binanceGet(
            path = AccountRoute.Deposit.path,
            withTimeout = false,
            parameters = parameters
        ).mapToState()
    }

    internal suspend fun getWithdrawalHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): ResultState<List<WithdrawResponse>> {
        val parameters = mutableListOf(
            "limit" to 1000.toString()
        )
        asset?.let { parameters += "coin" to asset }
        startTime?.let { parameters += "startTime" to startTime.toString() }
        endTime?.let { parameters += "endTime" to endTime.toString() }

        return httpClient.binanceGet(
            path = AccountRoute.Withdraw.path,
            withTimeout = false,
            parameters = parameters
        ).mapToState()
    }
}

private enum class AccountRoute(val path: String) {
    Deposit("${ApiVersion.Public.value}/capital/deposit/hisrec"),
    Withdraw("${ApiVersion.Public.value}/capital/withdraw/history"),

}