package velkonost.binance.sdk.client

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.model.DepositData
import velkonost.binance.sdk.model.WithdrawData

interface AccountService {
    suspend fun getDepositHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): BinanceResult<List<DepositData>>

    suspend fun getWithdrawHistory(
        asset: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): BinanceResult<List<WithdrawData>>

}