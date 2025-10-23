package velkonost.binance.sdk.client

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.model.FuturesBalance
import velkonost.binance.sdk.model.FuturesOpenPosition
import velkonost.binance.sdk.model.Kline

interface FuturesService {
    suspend fun ping(): BinanceResult<Boolean>

    suspend fun getBalance(asset: String): BinanceResult<FuturesBalance>

    suspend fun getOpenPositions(): BinanceResult<List<FuturesOpenPosition>>

    suspend fun getSymbols(): BinanceResult<List<String>>

    suspend fun getKlines(symbol: String, interval: KlineInterval, start: String): BinanceResult<List<Kline>>
}