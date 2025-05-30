package velkonost.binance.sdk.data.repository

import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import velkonost.binance.sdk.data.datasource.FuturesDataSource
import velkonost.binance.sdk.data.model.response.SocketResponse
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.socket.ConnectState
import velkonost.binance.sdk.network.socket.WSSession
import velkonost.binance.sdk.network.socket.onSuccess

typealias Symbol = String
/**
 * Repository class for managing WebSocket connections to Binance.
 * This class handles real-time market data streams and user data streams,
 * providing a clean interface for subscribing to and managing WebSocket connections.
 *
 * @property dataSource The data source for WebSocket operations
 * @property runningSockets A thread-safe map of active WebSocket connections
 * @property updatesFlow A shared flow that emits updates about the state of WebSocket connections
 */
internal class SocketRepository(
    apiKey: String, apiSecret: String,
    private val dataSource: FuturesDataSource = FuturesDataSource(apiKey, apiSecret)
) : Repository() {

    private val runningSockets: ConcurrentMutableMap<Symbol, WSSession<SocketResponse>> = ConcurrentMutableMap()
    private val updatesFlow = MutableSharedFlow<Map<Symbol, WSSession<SocketResponse>>>(replay = 1)

    /**
     * Emits the current state of all WebSocket connections to the [updatesFlow].
     * This is called whenever the state of connections changes.
     */
    private fun emitCurrentState() {
        updatesFlow.tryEmit(runningSockets.toMap())
    }

    /**
     * Starts a new WebSocket connection for a specific symbol.
     *
     * @param symbol The trading symbol to subscribe to
     * @param interval The candlestick interval for updates
     * @return A [WSSession] object for managing the WebSocket connection
     */
    private suspend fun startSocket(
        symbol: Symbol,
        interval: KlineInterval = KlineInterval.Minute1
    ): WSSession<SocketResponse> {
        val result = CompletableDeferred<WSSession<SocketResponse>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            val session = dataSource.launchWebSocket(symbol, interval)
            result.complete(session)
        }
        return result.await()
    }

    /**
     * Subscribes to incoming WebSocket updates for a specific trading symbol.
     * If a connection for the symbol already exists, it will be reused.
     * Only closed candlesticks are emitted through the flow.
     *
     * @param symbol The trading symbol to subscribe to
     * @param interval The candlestick interval for updates
     * @return A flow of [SocketResponse] objects containing real-time market data
     */
    suspend fun getSocketIncoming(symbol: Symbol, interval: KlineInterval = KlineInterval.Minute1): Flow<SocketResponse> {
        val session = runningSockets.getOrPut(symbol) {
            startSocket(symbol, interval).also { emitCurrentState() }
        }
        return session.incoming.onSuccess().filter { it.klineData.isKlineClosed }
    }

    /**
     * Starts listening to the state of all active WebSocket connections.
     * This is useful for monitoring connection health and debugging.
     * The state updates are emitted through the [updatesFlow].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun listenSocketsConnections() {
        updatesFlow.collect { sockets ->
            // Handle connection state updates
        }
    }

    /**
     * Closes the WebSocket connection for a specific symbol.
     * This will remove the connection from [runningSockets] and emit the updated state.
     *
     * @param symbol The trading symbol to unsubscribe from
     */
    suspend fun closeSocket(symbol: Symbol) {
        runningSockets.remove(symbol)?.let {
            emitCurrentState()
        }
    }
}