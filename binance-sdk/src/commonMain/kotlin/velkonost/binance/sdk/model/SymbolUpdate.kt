package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SocketKlineData
import velkonost.binance.sdk.data.model.response.SocketResponse

data class SymbolUpdate(
    val symbol: String,
    val time: Long,
    val kline: Kline
)

fun SocketKlineData.toKline() = Kline(
    open = openPrice.toDouble(),
    close = closePrice.toDouble(),
    high = highPrice.toDouble(),
    low = lowPrice.toDouble(),
    volume = volume.toDouble(),
)

fun SocketResponse.toSymbolUpdate(): SymbolUpdate = SymbolUpdate(
    symbol = pair,
    time = eventTime,
    kline = klineData.toKline()
)
