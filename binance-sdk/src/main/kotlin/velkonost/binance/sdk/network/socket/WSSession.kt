package velkonost.binance.sdk.network.socket

import kotlinx.coroutines.flow.Flow

internal interface WSSession<T> {
    /**
     * Incoming messages flow
     */
    val incoming: Flow<Result<T>>

    /**
     * Flush and close WS
     */
    suspend fun close()

    /**
     * WS network connect state. Empty before first connect
     */
    val connectState: Flow<ConnectState>

    /**
     * return true while not called [close] after return false.
     *
     * while socket closing also return false
     *
     * while socket not open first time also return true
     */
    val isOpen: Boolean
}


internal enum class ConnectState {
    Connected,
    Disconnected
}