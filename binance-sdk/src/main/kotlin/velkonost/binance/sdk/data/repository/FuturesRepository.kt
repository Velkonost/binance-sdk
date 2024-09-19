package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import velkonost.binance.sdk.data.datasource.FuturesDataSource
import velkonost.binance.sdk.data.model.response.*
import velkonost.binance.sdk.enums.KlineInterval
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.extensions.convertIntervalToMills
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.extensions.toMilliseconds
import velkonost.binance.sdk.network.handleError
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess
import kotlin.math.max

internal class FuturesRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: FuturesDataSource = FuturesDataSource(apiKey, apiSecret)
) : Repository() {

    fun ping(): Boolean {
        val result = CompletableDeferred<Boolean>()
        coroutineScope.launchCatching(catch = { result.complete(false) }) {
            dataSource.ping()
                .onSuccess { result.complete(true) }
                .onFailure { _, _ -> result.complete(false) }
        }

        return runBlocking { result.await() }
    }

    fun getBalance(asset: String): BalanceResponse {
        val result = CompletableDeferred<BalanceResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getBalance()
                .onSuccess { list ->
                    list.firstOrNull { it.asset == asset }?.let { result.complete(it) }
                        ?: result.handleError(BinanceSDKException("balance in $asset not found"))
                }
                .onFailure(result::handleError)
        }
        return runBlocking { result.await() }
    }

    fun getOpenPositions(): List<SymbolPositionResponse> {
        val result = CompletableDeferred<List<SymbolPositionResponse>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getPositions()
                .onSuccess { list ->
                    result.complete(list.filter { it.notional != 0.0f })
                }
                .onFailure(result::handleError)
        }
        return runBlocking { result.await() }
    }

    fun getExchangeSymbols(): List<ExchangeSymbolData> {
        val result = CompletableDeferred<List<ExchangeSymbolData>>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getExchangeInfo()
                .onSuccess { data ->
                    val eligibleSymbols = data.symbols.filter { "_" !in it.symbol && it.status == "TRADING" }
                    result.complete(eligibleSymbols)
                }
                .onFailure(result::handleError)
        }
        return runBlocking { result.await() }
    }

    fun getHistoricalKlines(
        symbol: String,
        interval: KlineInterval,
        start: String?,
        end: String? = null,
        limit: Int = 1000
    ): List<KlineResponse> {
        val result = CompletableDeferred<List<KlineResponse>>()
        val resultList = mutableListOf<KlineResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            val timeframe = interval.value.convertIntervalToMills()

            var convertedStartTime = start?.toMilliseconds() ?: System.currentTimeMillis()
            val firstKline = getEarliestValidKline(symbol, interval)
            val firstValidStartTime = firstKline.openTime.toLong()
            firstValidStartTime.let {
                convertedStartTime = max(convertedStartTime, firstValidStartTime)
            }

            val convertedEndTime = end?.toMilliseconds() ?: System.currentTimeMillis()
            if (convertedEndTime <= convertedStartTime) {
                result.complete(emptyList())
                return@launchCatching
            }

            var idx = 0
            var shouldBreak = false
            while (!shouldBreak) {
                dataSource.getKlines(
                    symbol = symbol,
                    interval = interval,
                    limit = limit,
                    startTime = convertedStartTime,
                    endTime = convertedEndTime
                ).onSuccess { list ->
                    val klines = list.map { it.mapToKline() }
                    resultList.addAll(klines)
                    convertedStartTime = klines.last().openTime.toLong() + timeframe

                    if (list.isEmpty() || list.size < limit || convertedStartTime >= convertedEndTime) {
                        shouldBreak = true
                    }
                }.onFailure { _, _ -> shouldBreak = true }

                idx++
                if (idx % 3 == 0) delay(3000)
            }
            result.complete(resultList)

        }

        return runBlocking { result.await() }
    }

    private fun getEarliestValidKline(
        symbol: String,
        interval: KlineInterval,
    ): KlineResponse {
        val result = CompletableDeferred<KlineResponse>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getKlines(
                symbol = symbol,
                interval = interval,
                limit = 1,
                startTime = 0,
                endTime = System.currentTimeMillis()
            ).onSuccess { list ->
                list.firstOrNull()?.let { klineData ->
                    val kline = klineData.mapToKline()
                    result.complete(kline)
                } ?: result.handleError(
                    BinanceSDKException("error find first valid kline for $symbol with interval ${interval.value}")
                )
            }.onFailure(result::handleError)
        }
        return runBlocking { result.await() }
    }

    fun getListenKey(): String {
        val result = CompletableDeferred<String>()
        coroutineScope.launchCatching(catch = result::handleError) {
            dataSource.getListenKey()
                .onSuccess { result.complete(it.listenKey) }
                .onFailure(result::handleError)
        }
        return runBlocking { result.await() }
    }

}