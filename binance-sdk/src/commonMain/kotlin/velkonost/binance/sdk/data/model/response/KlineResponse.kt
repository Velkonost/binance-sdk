package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

/**
 * Represents a candlestick (kline) data point from the Binance API.
 * This model is used for historical market data responses.
 *
 * @property openTime The start time of the candlestick period in milliseconds
 * @property open The opening price of the period
 * @property high The highest price reached during the period
 * @property low The lowest price reached during the period
 * @property close The closing price of the period
 * @property volume The trading volume during the period
 * @property closeTime The end time of the candlestick period in milliseconds
 * @property quoteAssetVolume The trading volume in the quote asset
 * @property numberOfTrades The number of trades during the period
 * @property takerBuyBaseAssetVolume The volume of trades where the taker was the buyer, in base asset
 * @property takerBuyQuoteAssetVolume The volume of trades where the taker was the buyer, in quote asset
 */
@Serializable
data class KlineResponse(
    val openTime: Float,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float,
    val closeTime: Float,
    val quoteAssetVolume: Float,
    val numberOfTrades: Float,
    val takerBuyBaseAssetVolume: Float,
    val takerBuyQuoteAssetVolume: Float
)

/**
 * Converts a list of float values from the API response to a [KlineResponse] object.
 * This extension function is used to parse the raw API response format.
 * The list must contain exactly 11 elements in the following order:
 * 1. Open time
 * 2. Open price
 * 3. High price
 * 4. Low price
 * 5. Close price
 * 6. Volume
 * 7. Close time
 * 8. Quote asset volume
 * 9. Number of trades
 * 10. Taker buy quote asset volume
 * 11. Taker buy base asset volume
 */
fun List<Float>.mapToKline(): KlineResponse {
    return KlineResponse(
        openTime = get(0),
        open = get(1),
        high = get(2),
        low = get(3),
        close = get(4),
        volume = get(5),
        closeTime = get(6),
        quoteAssetVolume = get(7),
        numberOfTrades = get(8),
        takerBuyQuoteAssetVolume = get(9),
        takerBuyBaseAssetVolume = get(10),
    )
}