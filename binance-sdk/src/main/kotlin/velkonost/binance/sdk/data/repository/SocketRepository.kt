package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import velkonost.binance.sdk.data.datasource.FuturesDataSource
import velkonost.binance.sdk.data.model.response.SocketResponse
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.socket.ConnectState
import velkonost.binance.sdk.network.socket.WSSession
import velkonost.binance.sdk.network.socket.onSuccess
import java.util.concurrent.ConcurrentHashMap

typealias Symbol = String

internal class SocketRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: FuturesDataSource = FuturesDataSource(apiKey, apiSecret)
) : Repository() {

    private val runningSockets: ConcurrentHashMap<Symbol, WSSession<SocketResponse>> = ConcurrentHashMap()
    private val updatesFlow = MutableSharedFlow<Map<Symbol, WSSession<SocketResponse>>>(replay = 1)

    private fun emitCurrentState() {
        updatesFlow.tryEmit(runningSockets.toMap())
    }

    private fun startSocket(
        symbol: Symbol,
        interval: KlineInterval = KlineInterval.Minute1
    ): WSSession<SocketResponse> {
        val result = CompletableDeferred<WSSession<SocketResponse>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            val session = dataSource.launchWebSocket(symbol, interval)
            result.complete(session)
        }
        return runBlocking { result.await() }
    }

    fun getSocketIncoming(symbol: Symbol, interval: KlineInterval = KlineInterval.Minute1): Flow<SocketResponse> {
        val session = runningSockets.getOrPut(symbol) {
            startSocket(symbol, interval).also { emitCurrentState() }
        }
        return session.incoming.onSuccess().filter { it.klineData.isKlineClosed }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun listenSocketsConnections() =
        updatesFlow
            .onEach { connections ->
                val total = connections.size
                val connected = connections.filter { it.value.connectState.first() == ConnectState.Connected }.size
                val disconnected = connections.filter { it.value.connectState.first() == ConnectState.Disconnected }.size
                println("Total sockets - $total | Connected - $connected | Disconnected - $disconnected")
            }
            .flatMapLatest { map ->
                map.entries.asFlow()
                    .flatMapMerge { entry ->
                        entry.value.connectState
                            .map { state -> entry.key to state }
                    }
            }.filter { it.second == ConnectState.Disconnected }
            .collect { (symbol, state) ->
                println("Connection state for $symbol: $state")
            }


    fun closeSocket(symbol: Symbol) {
        coroutineScope.launchCatching {
            runningSockets[symbol]?.close()?.let {
                runningSockets.remove(symbol).also { emitCurrentState() }
            }
        }
    }
}