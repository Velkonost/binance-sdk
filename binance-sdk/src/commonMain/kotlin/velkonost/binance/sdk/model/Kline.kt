package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.KlineResponse

/**
 * Represents a candlestick (kline) data point in the market.
 * This model contains the essential price and volume information for a specific time period.
 *
 * @property open The opening price of the period
 * @property high The highest price reached during the period
 * @property low The lowest price reached during the period
 * @property close The closing price of the period
 * @property volume The trading volume during the period
 */
data class Kline(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
)

/**
 * Converts a list of [KlineResponse] objects to a list of [Kline] objects.
 * This extension function is used to transform API responses into the SDK's domain model.
 */
fun List<KlineResponse>.toKlines() = map { it.toKline() }

/**
 * Converts a [KlineResponse] object to a [Kline] object.
 * This extension function transforms the API response format into the SDK's domain model.
 */
fun KlineResponse.toKline() = Kline(
    open = open.toDouble(),
    high = high.toDouble(),
    low = low.toDouble(),
    close = close.toDouble(),
    volume = volume.toDouble(),
)