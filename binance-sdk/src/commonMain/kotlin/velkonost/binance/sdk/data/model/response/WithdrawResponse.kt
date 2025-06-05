package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class WithdrawResponse(
    val address: String,
    val amount: Float,
    val applyTime: String, //"2019-10-12 11:12:02"
    val coin: String,
    val id: String,
    val network: String,
    val transferType: Int,   // 1 for internal transfer, 0 for external transfer
    val status: Int,
    val txId: String
)