package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.BalanceResponse
import java.math.BigDecimal

data class FuturesBalance(
    val all: BigDecimal,
    val crossWallet: BigDecimal,
    val available: BigDecimal
)

internal fun BalanceResponse.toFuturesBalance() = FuturesBalance(
    all = BigDecimal(balance),
    crossWallet = BigDecimal(crossWalletBalance),
    available = BigDecimal(availableBalance)
)