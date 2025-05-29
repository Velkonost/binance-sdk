package velkonost.binance.sdk.data.datasource

import io.ktor.client.*
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import velkonost.binance.sdk.enums.ApiVersion
import velkonost.binance.sdk.enums.Urls
import velkonost.binance.sdk.network.ResultState
import velkonost.binance.sdk.network.extensions.get

internal class MainDataSource(apiKey: String, apiSecret: String) : DataSource(apiKey, apiSecret) {

    override val httpClient: HttpClient by inject() {
        parametersOf(Urls.Api.value, getHeaders())
    }

    internal suspend fun ping(): ResultState<Unit> =
        httpClient.get(MainRoute.Ping.path).mapToState()

}

private enum class MainRoute(val path: String) {
    Ping("${ApiVersion.Private.value}/ping")
}