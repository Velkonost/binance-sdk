package velkonost.binance.sdk.client

import kotlinx.coroutines.flow.Flow
import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.data.repository.Symbol
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.model.SymbolUpdate

interface SocketService {
    suspend fun socketListenAllSymbols(
        delayBetweenLaunches: Long = 1_000,
        interval: KlineInterval = KlineInterval.Minute1,
        logConnectionsState: Boolean = true,
        maxConcurrency: Int = 20
    ): BinanceResult<Flow<SymbolUpdate>>

    suspend fun socketListenAllSymbolsAdvanced(
        delayBetweenLaunches: Long = 1_000,
        interval: KlineInterval = KlineInterval.Minute1,
        logConnectionsState: Boolean = true,
        maxConcurrency: Int = 20,
        bufferSize: Int = 64,
        conflateUpdates: Boolean = false
    ): BinanceResult<Flow<SymbolUpdate>>

    suspend fun socketListenUpdates(symbol: Symbol, interval: KlineInterval): BinanceResult<Flow<SymbolUpdate>>

    suspend fun socketStopListenUpdates(symbol: Symbol): BinanceResult<Unit>
}