package velkonost.binance.sdk.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import velkonost.binance.sdk.data.repository.FuturesRepository
import velkonost.binance.sdk.data.repository.MainRepository
import velkonost.binance.sdk.data.repository.SocketRepository
import velkonost.binance.sdk.data.repository.Symbol
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.model.*

internal class Client(
    apiKey: String, apiSecret: String,
    private val mainRepository: MainRepository = MainRepository(apiKey, apiSecret),
    private val futuresRepository: FuturesRepository = FuturesRepository(apiKey, apiSecret),
    private val socketRepository: SocketRepository = SocketRepository(apiKey, apiSecret)
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // SOCKET
    internal suspend fun socketListenAllSymbols(
        delayBetweenLaunches: Long,
        interval: KlineInterval,
        logConnectionsState: Boolean,
        collector: ((SymbolUpdate) -> Unit)? = null
    ): List<Flow<SymbolUpdate>> {
        val result = CompletableDeferred<List<Flow<SymbolUpdate>>>()
        val sockets = mutableListOf<Flow<SymbolUpdate>>()
        val symbols = futuresExchangeSymbols()

        if (logConnectionsState) {
            socketListenConnections()
        }

        coroutineScope.launch {
            symbols.forEach { symbol ->
                delay(delayBetweenLaunches)

                val newSocket = async { socketListenUpdates(symbol, interval) }.await()
                collector?.let { launch { newSocket.collect(it) } }

                sockets.add(newSocket)
            }
            result.complete(sockets)
        }
        return result.await()
    }

    internal fun socketListenConnections() = coroutineScope.launch { socketRepository.listenSocketsConnections() }
    internal suspend fun socketListenUpdates(symbol: Symbol, interval: KlineInterval) =
        socketRepository.getSocketIncoming(symbol, interval).map { it.toSymbolUpdate() }

    internal fun socketStopListenUpdates(symbol: Symbol) = socketRepository.closeSocket(symbol)


    // GENERAL
    internal suspend fun ping() = mainRepository.ping()

    // FUTURES
    internal suspend fun futuresPing() = futuresRepository.ping()
    internal suspend fun futuresGetBalance(asset: String): FuturesBalance {
        val result = futuresRepository.getBalance(asset)
        return result.toFuturesBalance()
    }

    internal suspend fun futuresOpenPosition(): List<FuturesOpenPosition> {
        val result = futuresRepository.getOpenPositions()
        return result.toFuturesOpenPositions()
    }

    internal suspend fun futuresExchangeSymbols(): List<Symbol> {
        val result = futuresRepository.getExchangeSymbols()
        return result.map { it.symbol }
    }

    internal suspend fun futuresHistoricalKlines(symbol: Symbol, interval: KlineInterval, start: String): List<Kline> {
        val result = futuresRepository.getHistoricalKlines(symbol, interval, start)
        return result.toKlines()
    }
}