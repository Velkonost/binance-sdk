package velkonost.binance.sdk.client.internal

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.client.GeneralService
import velkonost.binance.sdk.data.repository.MainRepository
import velkonost.binance.sdk.exception.NetworkException

internal class GeneralServiceImpl(
    private val repository: MainRepository
) : GeneralService {

    override suspend fun ping(): BinanceResult<Boolean> = try {
        BinanceResult.Success(repository.ping())
    } catch (e: Exception) {
        BinanceResult.Failure(NetworkException("Failed to ping", e))
    }
}