package velkonost.binance.sdk.client

import velkonost.binance.sdk.BinanceResult

interface GeneralService {
    suspend fun ping(): BinanceResult<Boolean>
}