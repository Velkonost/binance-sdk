package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.KlineResponse

data class Kline(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
)

fun List<KlineResponse>.toKlines() = map { it.toKline() }
fun KlineResponse.toKline() = Kline(
    open = open.toDouble(),
    high = high.toDouble(),
    low = low.toDouble(),
    close = close.toDouble(),
    volume = volume.toDouble(),
)