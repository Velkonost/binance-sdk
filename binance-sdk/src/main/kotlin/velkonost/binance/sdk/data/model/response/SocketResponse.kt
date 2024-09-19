package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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