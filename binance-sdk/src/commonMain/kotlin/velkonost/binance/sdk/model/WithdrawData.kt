package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.WithdrawResponse
import velkonost.binance.sdk.extensions.convertToMillis

data class WithdrawData(
    val address: String,
    val amount: Float,
    val coin: String,
    val network: String,
    val datetime: Long
)

internal fun WithdrawResponse.toWithdrawData() = WithdrawData(
    address, amount, coin, network, convertToMillis(applyTime)
)