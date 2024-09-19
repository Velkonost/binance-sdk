package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class ExchangeInfoResponse(
    val timezone: String,
    val serverTime: Long,
    val futuresType: String,
    val rateLimits: List<ExchangeRateLimits>,
    val symbols: List<ExchangeSymbolData>
)

@Serializable
internal data class ExchangeSymbolData(
    val symbol: String,
    val status: String
)

@Serializable
internal data class ExchangeRateLimits(
    val rateLimitType: String,
    val interval: String,
    val intervalNum: Int,
    val limit: Int
)