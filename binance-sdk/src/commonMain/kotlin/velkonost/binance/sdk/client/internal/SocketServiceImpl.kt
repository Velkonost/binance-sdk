package velkonost.binance.sdk.client.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.asResult
import velkonost.binance.sdk.client.SocketService
import velkonost.binance.sdk.data.repository.FuturesRepository
import velkonost.binance.sdk.data.repository.SocketRepository
import velkonost.binance.sdk.data.repository.Symbol
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.exception.NetworkException
import velkonost.binance.sdk.model.SymbolUpdate
import velkonost.binance.sdk.model.toSymbolUpdate

internal class SocketServiceImpl(
    private val repository: SocketRepository,
    private val futuresRepository: FuturesRepository
) : SocketService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun socketListenAllSymbols(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        maxConcurrency: Int
    ): BinanceResult<Flow<SymbolUpdate>> {
        if (logConnectionsState) {
            socketListenConnections()
        }

        val symbols = try {
            futuresRepository.getExchangeSymbols()
        } catch (e: Exception) {
            return BinanceResult.Failure(NetworkException("Fail get symbols", e))
        }

        val symbolsFlow = flow {
            symbols.forEachIndexed { index, symbol ->
                if (index > 0) {
                    delay(delayBetweenLaunches)
                }
                emit(symbol.symbol)
            }
        }

        return symbolsFlow
            .flatMapMerge(concurrency = maxConcurrency) { symbol ->
                try {
                    socketListenUpdates(symbol, interval).getOrThrow()
                        .catch { e ->
                            println("Error in socket for symbol $symbol: ${e.message}")
                        }
                } catch (e: Exception) {
                    println("Failed to create socket for symbol $symbol: ${e.message}")
                    flowOf()
                }
            }
            .flowOn(Dispatchers.Default)
            .asResult()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun socketListenAllSymbolsAdvanced(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        maxConcurrency: Int,
        bufferSize: Int,
        conflateUpdates: Boolean
    ): BinanceResult<Flow<SymbolUpdate>> {
        if (logConnectionsState) {
            socketListenConnections()
        }

        val symbols = try {
            futuresRepository.getExchangeSymbols()
        } catch (e: Exception) {
            return BinanceResult.Failure(NetworkException("Fail get symbols", e))
        }

        val symbolsFlow = flow {
            symbols.forEachIndexed { index, symbol ->
                if (index > 0) {
                    delay(delayBetweenLaunches)
                }
                emit(symbol.symbol)
            }
        }

        return symbolsFlow
            .flatMapMerge(concurrency = maxConcurrency) { symbol ->
                try {
                    val symbolFlow = socketListenUpdates(symbol, interval)
                        .getOrThrow()
                        .catch { e ->
                            println("Error in socket for symbol $symbol: ${e.message}")
                        }

                    when {
                        conflateUpdates -> symbolFlow.conflate()
                        else -> symbolFlow.buffer(bufferSize)
                    }
                } catch (e: Exception) {
                    println("Failed to create socket for symbol $symbol: ${e.message}")
                    flowOf()
                }
            }
            .flowOn(Dispatchers.Default)
            .asResult()
    }

    override suspend fun socketListenUpdates(
        symbol: Symbol,
        interval: KlineInterval
    ): BinanceResult<Flow<SymbolUpdate>> = try {
        BinanceResult.Success(repository.getSocketIncoming(symbol, interval).map { it.toSymbolUpdate() })
    } catch (e: Exception) {
        BinanceResult.Failure(BinanceSDKException("Error in socketListenUpdates with symbol=$symbol", e))
    }

    override suspend fun socketStopListenUpdates(symbol: Symbol): BinanceResult<Unit> = try {
        BinanceResult.Success(repository.closeSocket(symbol))
    } catch (e: Exception) {
        BinanceResult.Failure(BinanceSDKException("Error in socketStopListenUpdates with symbol=$symbol", e))
    }


    private suspend fun socketListenConnections() = repository.listenSocketsConnections()
}