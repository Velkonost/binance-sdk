package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import velkonost.binance.sdk.enums.ApiVersion
import velkonost.binance.sdk.enums.Urls
import velkonost.binance.sdk.network.ResultState
import velkonost.binance.sdk.network.extensions.get

internal class MainDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {

    override val httpClient: HttpClient by inject(HttpClient::class.java) {
        parametersOf(Urls.Api.value, getHeaders())
    }

    internal suspend fun ping(): ResultState<Unit> = withContext(Dispatchers.IO) {
        httpClient.get(MainRoute.Ping.path).mapToState()
    }
}

private enum class MainRoute(val path: String) {
    Ping("${ApiVersion.Private.value}/ping")
}