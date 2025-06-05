package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class DepositResponse(
    val amount: Float,
    val coin: String,
    val network: String,
    val status: Int,
    val address: String,
    val addressTag: String,
    val txId: String,
    val insertTime: Long,
    val transferType: Int,
    val confirmTimes: String
)