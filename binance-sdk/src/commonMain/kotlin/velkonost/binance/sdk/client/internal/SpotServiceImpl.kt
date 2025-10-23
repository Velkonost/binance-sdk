package velkonost.binance.sdk.client.internal

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.client.SpotService
import velkonost.binance.sdk.data.repository.SpotRepository
import velkonost.binance.sdk.exception.BinanceSDKException

internal class SpotServiceImpl(
    private val repository: SpotRepository
) : SpotService {
    override suspend fun spotGetAllBalance(asset: String?) = runCatching {
        BinanceResult.Success(repository.getBalance(asset))
    }
        .onFailure { BinanceResult.Failure(BinanceSDKException("Error", it)) }
        .getOrThrow()

    override suspend fun spotGetTrades(
        asset: String,
        startTime: Long?,
        endTime: Long?
    ) = runCatching {
        BinanceResult.Success(repository.getTrades(asset, startTime, endTime))
    }
        .onFailure { BinanceResult.Failure(BinanceSDKException("Error", it)) }
        .getOrThrow()

}