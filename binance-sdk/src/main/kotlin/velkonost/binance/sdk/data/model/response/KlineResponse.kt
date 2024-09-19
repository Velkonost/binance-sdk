package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

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