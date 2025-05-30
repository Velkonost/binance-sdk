package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CompletableDeferred
import velkonost.binance.sdk.data.datasource.MainDataSource
import velkonost.binance.sdk.extensions.launchCatching
import velkonost.binance.sdk.network.onFailure
import velkonost.binance.sdk.network.onSuccess

/**
 * The `MainRepository` class provides access to general Binance API endpoints.
 * It is responsible for operations such as health checks and other non-specific API calls.
 *
 * @param apiKey The API key for authentication.
 * @param apiSecret The secret key for signing requests.
 */
internal class MainRepository(
    apiKey: String, apiSecret: String,
    override val dataSource: MainDataSource = MainDataSource(apiKey, apiSecret)
) : Repository() {

    /**
     * Pings the Binance API to check connectivity.
     * This method is used to verify that the API is reachable.
     */
    suspend fun ping(): Boolean {
        val result = CompletableDeferred<Boolean>()
        coroutineScope.launchCatching(catch = { result.complete(false) }) {
            dataSource.ping()
                .onSuccess { result.complete(true) }
                .onFailure { _, _ -> result.complete(false) }
        }

        return result.await()
    }
}