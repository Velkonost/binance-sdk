package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AccountResponse(
    val makerCommission: Int,
    val takerCommission: Int,
    val buyerCommission: Int,
    val sellerCommission: Int,
    val canTrade: Boolean,
    val canWithdraw: Boolean,
    val canDeposit: Boolean,
    val balances: List<AccountBalanceResponse>
)

@Serializable
internal data class AccountBalanceResponse(
    val asset: String,
    val free: Float,
    val locked: Float
)