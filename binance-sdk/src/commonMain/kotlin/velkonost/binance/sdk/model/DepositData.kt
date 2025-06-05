package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.DepositResponse

data class DepositData(
    val amount: Float,
    val coin: String,
    val network: String,
    val address: String,
    val addressTag: String,
    val txId: String,
    val datetime: Long,
)

internal fun DepositResponse.toDepositData() = DepositData(
    amount, coin, network, address, addressTag, txId, insertTime
)