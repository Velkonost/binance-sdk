package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SpotTradesResponse

data class SpotTrade(
    val id: Long,
    val symbol: String,
    val price: Float,
    val qty: Float,
    val commission: String,
    val commissionAsset: String,
    val time: Long,
    val isBuyer: Boolean
)

internal fun SpotTradesResponse.toSpotTrade() = SpotTrade(
    id, symbol, price.toFloat(), qty.toFloat(), commission, commissionAsset, time, isBuyer
)