package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.KlineResponse
import java.math.BigDecimal

data class Kline(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
)

fun List<KlineResponse>.toKlines() = map { it.toKline() }
fun KlineResponse.toKline() = Kline(
    open = open.toBigDecimal(),
    high = high.toBigDecimal(),
    low = low.toBigDecimal(),
    close = close.toBigDecimal(),
    volume = volume.toBigDecimal(),
)