package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import velkonost.binance.sdk.data.datasource.AccountDataSource
import velkonost.binance.sdk.data.datasource.SpotDataSource
import velkonost.binance.sdk.data.model.response.DepositResponse
import velkonost.binance.sdk.data.model.response.SpotTradesResponse
import velkonost.binance.sdk.data.model.response.WithdrawResponse
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.model.*
import velkonost.binance.sdk.model.toDepositData
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess

internal class AccountRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: AccountDataSource = AccountDataSource(apiKey, apiSecret)
) : Repository() {

    suspend fun getDeposit(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<DepositData> {
        val result = CompletableDeferred<List<DepositData>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getDepositHistory(
                asset = asset,
                startTime = startTime,
                endTime = endTime
            )
                .onSuccess { data ->
                    result.complete(data.map(DepositResponse::toDepositData))
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

    suspend fun getWithdraw(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<WithdrawData> {
        val result = CompletableDeferred<List<WithdrawData>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getWithdrawalHistory(
                asset = asset,
                startTime = startTime,
                endTime = endTime
            )
                .onSuccess { data ->
                    result.complete(data.map(WithdrawResponse::toWithdrawData))
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

}