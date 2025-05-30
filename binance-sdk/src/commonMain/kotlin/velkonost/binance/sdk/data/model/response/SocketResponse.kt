package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a WebSocket message from the Binance API.
 * This model is used for real-time market data updates through WebSocket connections.
 *
 * @property eventType The type of event (e.g., "kline")
 * @property eventTime The timestamp of the event in milliseconds
 * @property pair The trading pair (e.g., "BTCUSDT")
 * @property contractType The type of contract (e.g., "PERPETUAL")
 * @property klineData The candlestick data for this update
 */
@Serializable
data class SocketResponse(
    @SerialName("e")
    val eventType: String,
    @SerialName("E")
    val eventTime: Long,
    @SerialName("ps")
    val pair: String,
    @SerialName("ct")
    val contractType: String,
    @SerialName("k")
    val klineData: SocketKlineData
)

/**
 * Represents the candlestick data within a WebSocket message.
 * This model contains detailed information about a single candlestick period.
 *
 * @property startTime The start time of the candlestick period in milliseconds
 * @property closeTime The end time of the candlestick period in milliseconds
 * @property interval The candlestick interval (e.g., "1m", "5m")
 * @property firstUpdateId The first update ID in this event
 * @property lastUpdateId The last update ID in this event
 * @property openPrice The opening price of the period
 * @property closePrice The closing price of the period
 * @property highPrice The highest price reached during the period
 * @property lowPrice The lowest price reached during the period
 * @property volume The trading volume during the period
 * @property numberOfTrades The number of trades during the period
 * @property isKlineClosed Whether this candlestick period is closed
 * @property quoteAssetVolume The trading volume in the quote asset
 * @property takerBuyVolume The volume of trades where the taker was the buyer
 * @property takerBuyQuoteAssetVolume The quote asset volume of trades where the taker was the buyer
 * @property ignore Reserved field for future use
 */
@Serializable
data class SocketKlineData(
    @SerialName("t")
    val startTime: Long,
    @SerialName("T")
    val closeTime: Long,
    @SerialName("i")
    val interval: String,
    @SerialName("f")
    val firstUpdateId: Long,
    @SerialName("L")
    val lastUpdateId: Long,
    @SerialName("o")
    val openPrice: Float,
    @SerialName("c")
    val closePrice: Float,
    @SerialName("h")
    val highPrice: Float,
    @SerialName("l")
    val lowPrice: Float,
    @SerialName("v")
    val volume: Float,
    @SerialName("n")
    val numberOfTrades: Float,
    @SerialName("x")
    val isKlineClosed: Boolean,
    @SerialName("q")
    val quoteAssetVolume: Float,
    @SerialName("V")
    val takerBuyVolume: Float,
    @SerialName("Q")
    val takerBuyQuoteAssetVolume: Float,
    @SerialName("B")
    val ignore: String
)