package velkonost.binance.sdk.client

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.model.SpotBalance
import velkonost.binance.sdk.model.SpotTrade

interface SpotService {

    suspend fun spotGetAllBalance(asset: String? = null): BinanceResult<List<SpotBalance>>

    suspend fun spotGetTrades(
        asset: String,
        startTime: Long? = null,
        endTime: Long? = null
    ): BinanceResult<List<SpotTrade>>

}