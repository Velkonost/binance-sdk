package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SocketKlineData
import velkonost.binance.sdk.data.model.response.SocketResponse

/**
 * Represents a real-time update for a trading symbol.
 * This model is used for WebSocket streams to deliver live market data updates.
 *
 * @property symbol The trading symbol (e.g., "BTCUSDT")
 * @property time The timestamp of the update in milliseconds
 * @property kline The candlestick data for this update
 */
data class SymbolUpdate(
    val symbol: String,
    val time: Long,
    val kline: Kline
)

/**
 * Converts a [SocketKlineData] object to a [Kline] object.
 * This extension function transforms WebSocket data into the SDK's domain model.
 */
fun SocketKlineData.toKline() = Kline(
    open = openPrice.toDouble(),
    close = closePrice.toDouble(),
    high = highPrice.toDouble(),
    low = lowPrice.toDouble(),
    volume = volume.toDouble(),
)

/**
 * Converts a [SocketResponse] object to a [SymbolUpdate] object.
 * This extension function transforms WebSocket messages into the SDK's domain model.
 */
fun SocketResponse.toSymbolUpdate(): SymbolUpdate = SymbolUpdate(
    symbol = pair,
    time = eventTime,
    kline = klineData.toKline()
)
