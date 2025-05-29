package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.BalanceResponse

data class FuturesBalance(
    val all: Double,
    val crossWallet: Double,
    val available: Double
)

internal fun BalanceResponse.toFuturesBalance() = FuturesBalance(
    all = balance.toDouble(),
    crossWallet = crossWalletBalance.toDouble(),
    available = availableBalance.toDouble()
)