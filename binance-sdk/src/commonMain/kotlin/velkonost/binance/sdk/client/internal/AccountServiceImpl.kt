package velkonost.binance.sdk.client.internal

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.client.AccountService
import velkonost.binance.sdk.data.repository.AccountRepository
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.model.DepositData
import velkonost.binance.sdk.model.WithdrawData

internal class AccountServiceImpl(
    private val repository: AccountRepository
) : AccountService {
    override suspend fun getDepositHistory(
        asset: String?,
        startTime: Long?,
        endTime: Long?
    ): BinanceResult<List<DepositData>> = runCatching {
        BinanceResult.Success(repository.getDeposit(asset, startTime, endTime))
    }
        .onFailure { BinanceResult.Failure(BinanceSDKException("Error", it)) }
        .getOrThrow()

    override suspend fun getWithdrawHistory(
        asset: String?,
        startTime: Long?,
        endTime: Long?
    ): BinanceResult<List<WithdrawData>> = runCatching {
        BinanceResult.Success(repository.getWithdraw(asset, startTime, endTime))
    }
        .onFailure { BinanceResult.Failure(BinanceSDKException("Error", it)) }
        .getOrThrow()
}