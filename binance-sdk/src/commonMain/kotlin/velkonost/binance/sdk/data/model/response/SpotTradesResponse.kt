package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal class SpotTradesResponse(
    val symbol: String,
    val id: Long,
    val price: String,
    val qty: String,
    val commission: String,
    val commissionAsset: String,
    val time: Long,
    val isBuyer: Boolean
)