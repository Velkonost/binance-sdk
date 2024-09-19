package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SymbolPositionResponse(
    val symbol: String,
    @SerialName("positionAmt")
    val positionAmount: String,
    val entryPrice: String,
    val breakEvenPrice: String,
    val markPrice: String,
    val unRealizedProfit: String,
    val liquidationPrice: String,
    val leverage: String,
    val maxNotionalValue: String,
    val marginType: String,
    val isolatedMargin: String,
    val isAutoAddMargin: String,
    val positionSide: String,
    val notional: Float,
    val isolatedWallet: String,
    val updateTime: Long,
    val isolated: Boolean,
    val adlQuantile: Int
)