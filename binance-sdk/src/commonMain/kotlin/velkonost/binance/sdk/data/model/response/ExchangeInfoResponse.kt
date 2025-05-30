package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

/**
 * Represents the exchange information response from the Binance API.
 * This model contains general information about the exchange, including timezone,
 * server time, rate limits, and available trading symbols.
 *
 * @property timezone The timezone of the exchange server
 * @property serverTime The current server time in milliseconds
 * @property futuresType The type of futures trading available
 * @property rateLimits List of rate limits applied to API requests
 * @property symbols List of available trading symbols and their specifications
 */
@Serializable
internal data class ExchangeInfoResponse(
    val timezone: String,
    val serverTime: Long,
    val futuresType: String,
    val rateLimits: List<ExchangeRateLimits>,
    val symbols: List<ExchangeSymbolData>
)

/**
 * Represents information about a trading symbol on the exchange.
 * This model contains basic information about a trading pair.
 *
 * @property symbol The trading pair symbol (e.g., "BTCUSDT")
 * @property status The current status of the symbol (e.g., "TRADING", "HALT")
 */
@Serializable
internal data class ExchangeSymbolData(
    val symbol: String,
    val status: String
)

/**
 * Represents rate limit information for API requests.
 * This model defines the restrictions on API usage to prevent abuse.
 *
 * @property rateLimitType The type of rate limit (e.g., "REQUEST_WEIGHT", "ORDERS")
 * @property interval The time interval for the rate limit
 * @property intervalNum The number of intervals
 * @property limit The maximum number of requests allowed within the interval
 */
@Serializable
internal data class ExchangeRateLimits(
    val rateLimitType: String,
    val interval: String,
    val intervalNum: Int,
    val limit: Int
)