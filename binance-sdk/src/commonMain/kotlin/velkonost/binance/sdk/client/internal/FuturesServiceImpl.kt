package velkonost.binance.sdk.client.internal

import velkonost.binance.sdk.BinanceResult
import velkonost.binance.sdk.BinanceValidator
import velkonost.binance.sdk.client.FuturesService
import velkonost.binance.sdk.data.repository.FuturesRepository
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.NetworkException
import velkonost.binance.sdk.model.*

internal class FuturesServiceImpl(
    private val repository: FuturesRepository
) : FuturesService {

    override suspend fun ping(): BinanceResult<Boolean> = try {
        BinanceResult.Success(repository.ping())
    } catch (e: Exception) {
        BinanceResult.Failure(NetworkException("Failed to ping futures", e))
    }

    override suspend fun getBalance(asset: String): BinanceResult<FuturesBalance> = try {
        BinanceValidator.validateAsset(asset).flatMap { validatedAsset ->
            BinanceResult.Success(repository.getBalance(validatedAsset).toFuturesBalance())
        }
    } catch (e: Exception) {
        BinanceResult.Failure(NetworkException("Failed to get balance", e))
    }

    override suspend fun getOpenPositions(): BinanceResult<List<FuturesOpenPosition>> = try {
        BinanceResult.Success(repository.getOpenPositions().toFuturesOpenPositions())
    } catch (e: Exception) {
        BinanceResult.Failure(NetworkException("Failed to get open positions", e))
    }

    override suspend fun getSymbols(): BinanceResult<List<String>> = try {
        BinanceResult.Success(repository.getExchangeSymbols().map { it.symbol })
    } catch (e: Exception) {
        BinanceResult.Failure(NetworkException("Failed to get symbols", e))
    }

    override suspend fun getKlines(symbol: String, interval: KlineInterval, start: String): BinanceResult<List<Kline>> =
        try {
            BinanceValidator.validateSymbol(symbol).flatMap { validatedSymbol ->
                BinanceResult.Success(
                    repository.getHistoricalKlines(
                        symbol = validatedSymbol,
                        interval = interval,
                        start = start
                    ).toKlines()
                )
            }
        } catch (e: Exception) {
            BinanceResult.Failure(NetworkException("Failed to get klines", e))
        }
}