package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import velkonost.binance.sdk.data.datasource.MainDataSource
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess

internal class MainRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: MainDataSource = MainDataSource(apiKey, apiSecret)
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
}