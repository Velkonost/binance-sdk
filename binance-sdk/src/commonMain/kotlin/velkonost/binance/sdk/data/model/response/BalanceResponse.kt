package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

/**
 * Represents the balance information for an asset in a futures account.
 * This model contains detailed information about the asset's balance, including
 * cross margin balance, available balance, and withdrawal limits.
 *
 * @property accountAlias The alias of the account
 * @property asset The asset symbol (e.g., "USDT", "BTC")
 * @property balance The total balance of the asset
 * @property crossWalletBalance The balance in the cross margin wallet
 * @property crossUnPnl The unrealized profit/loss in cross margin
 * @property availableBalance The available balance for trading
 * @property maxWithdrawAmount The maximum amount available for withdrawal
 * @property marginAvailable Whether margin trading is available for this asset
 * @property updateTime The timestamp of the last balance update
 */
@Serializable
internal data class BalanceResponse(
    val accountAlias: String,
    val asset: String,
    val balance: String,
    val crossWalletBalance: String,
    val crossUnPnl: String,
    val availableBalance: String,
    val maxWithdrawAmount: String,
    val marginAvailable: Boolean,
    val updateTime: Long
)