package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import velkonost.binance.sdk.data.datasource.SpotDataSource
import velkonost.binance.sdk.data.model.response.AccountBalanceResponse
import velkonost.binance.sdk.data.model.response.SpotTradesResponse
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.model.SpotBalance
import velkonost.binance.sdk.model.SpotTrade
import velkonost.binance.sdk.model.toSpotBalance
import velkonost.binance.sdk.model.toSpotTrade
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess

internal class SpotRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: SpotDataSource = SpotDataSource(apiKey, apiSecret)
) : Repository() {

    suspend fun getBalance(asset: String? = null): List<SpotBalance> {
        val result = CompletableDeferred<List<SpotBalance>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getAccount()
                .onSuccess { data ->
                    if (asset.isNullOrBlank()) {
                        result.complete(data.balances.map(AccountBalanceResponse::toSpotBalance))
                    } else {
                        data.balances.firstOrNull { it.asset == asset }
                            ?.let { result.complete(listOf(it.toSpotBalance())) }
                            ?: result.handleError(BinanceSDKException("balance in $asset not found"))
                    }
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }

    suspend fun getTrades(asset: String?): List<SpotTrade> {
        val result = CompletableDeferred<List<SpotTrade>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getTrades("${asset}USDT")
                .onSuccess { data ->
                    result.complete(data.map(SpotTradesResponse::toSpotTrade))
                }
                .onFailure(result::handleError)
        }
        return result.await()
    }
}