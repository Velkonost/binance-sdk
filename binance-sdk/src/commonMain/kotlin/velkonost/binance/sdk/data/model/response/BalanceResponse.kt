package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

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