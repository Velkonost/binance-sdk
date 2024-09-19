package velkonost.binance.sdk.network.extensions

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal suspend inline fun HttpClient.get(
    path: String,
    parameters: List<Pair<String, String>> = emptyList()
) = get {
    url {
        path(path)
        parameters.forEach { parameter(it.first, it.second) }
    }
}

internal suspend inline fun HttpClient.post(
    path: String,
) = post {
    url { path(path) }
}