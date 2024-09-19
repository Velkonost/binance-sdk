package velkonost.binance.sdk.network.socket

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

internal inline fun <reified T> HttpClient.wsSession(
    url: String,
): WSSession<T> = object : WSSession<T> {
    private var isClosed = false
    private var innerWSSession: WebSocketSession? = null

    override val connectState = MutableSharedFlow<ConnectState>(1)
    override val isOpen: Boolean get() = !isClosed

    override val incoming: Flow<Result<T>> = flow<Result<T>> {
        if (isClosed) return@flow
        val ws = webSocketSession(urlString = url)
        innerWSSession = ws
        if (isClosed) {
            ws.close()
            emit(Result.failure(NoData))
            return@flow
        }
        connectState.emit(ConnectState.Connected)
        emitAll(ws.incoming.receiveAsFlow().map { frame -> requestWrapperWs(frame) })
    }.retry { cause: Throwable ->
        if (cause is IOException || cause is HttpRequestTimeoutException) {
            connectState.emit(ConnectState.Disconnected)
        }
        true
    }.filter { isOpen }

    override suspend fun close() {
        isClosed = true
        innerWSSession?.close()
        connectState.emit(ConnectState.Disconnected)
    }

}

private inline fun <reified T> requestWrapperWs(
    frame: Frame,
): Result<T> = runCatching {
    when (frame) {
        is Frame.Close -> throw CancellationException(frame.data.decodeToString())
        is Frame.Ping -> throw NoData
        is Frame.Pong -> throw NoData
        is Frame.Text -> commonJsonConfig.decodeFromString(frame.readText())
        is Frame.Binary -> commonJsonConfig.decodeFromString(frame.data.decodeToString())
        else -> throw NoData
    }
}

private val commonJsonConfig = Json {
    ignoreUnknownKeys = true
    allowSpecialFloatingPointValues = true
}

/**
 * no data - Anywhere. When data not set
 */
object NoData : Throwable() {
    private fun readResolve(): Any = NoData
}

internal inline fun <reified T : Any> Flow<Result<T>>.onSuccess(): Flow<T> {
    return this.mapNotNull { result ->
        result.getOrNull()?.takeIf { result.isSuccess && it::class.java == T::class.java }
    }
}