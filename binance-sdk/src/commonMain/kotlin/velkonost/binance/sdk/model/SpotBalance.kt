package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.AccountBalanceResponse

data class SpotBalance(
    val asset: String,
    val free: Float,
    val locked: Float
)

internal fun AccountBalanceResponse.toSpotBalance() = SpotBalance(
    asset, free, locked
)