package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.BalanceResponse

/**
 * Represents the balance information for a futures trading account.
 * This model contains the total balance, cross wallet balance, and available balance for trading.
 *
 * @property all The total balance including unrealized profit/loss
 * @property crossWallet The balance in the cross margin wallet
 * @property available The available balance for trading (excluding margin used in positions)
 */
data class FuturesBalance(
    val all: Double,
    val crossWallet: Double,
    val available: Double
)

/**
 * Converts a [BalanceResponse] object to a [FuturesBalance] object.
 * This extension function transforms the API response format into the SDK's domain model.
 */
internal fun BalanceResponse.toFuturesBalance() = FuturesBalance(
    all = balance.toDouble(),
    crossWallet = crossWalletBalance.toDouble(),
    available = availableBalance.toDouble()
)