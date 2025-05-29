package velkonost.binance.sdk.data.datasource

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA256
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import velkonost.binance.sdk.network.ResultState
import velkonost.binance.sdk.network.extensions.get

internal abstract class DataSource(
    private val apiKey: String,
    private val apiSecret: String
) : KoinComponent {

    abstract val httpClient: HttpClient

    protected fun getHeaders() = listOf(
        Pair(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
        ),
        Pair("X-MBX-APIKEY", apiKey)
    )

    protected suspend inline fun <reified T> HttpResponse.mapToState(): ResultState<T> =
        if (status.value in 200..299) {
            ResultState.Success(body<T>())
        } else {
            ResultState.Failure(
                code = status.value,
                message = body()
            )
        }

    protected suspend inline fun HttpClient.binanceGet(
        path: String,
        parameters: List<Pair<String, String>> = emptyList(),
    ): HttpResponse {
        val defaultParameters = listOf(
            "timestamp" to Clock.System.now().toEpochMilliseconds().toString(),
            "timeout" to 20.toString()
        )
        val totalParameters = parameters + defaultParameters
        val signatureParameter = "signature" to generateSignature(totalParameters)
        return get(path, totalParameters + signatureParameter)
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected suspend fun generateSignature(parameters: List<Pair<String, String>>): String {
        val queryString = parameters.joinToString("&") { "${it.first}=${it.second}" }
        val hmacSha256: ByteArray
        try {
            val key = CryptographyProvider.Default.get(HMAC).keyDecoder(SHA256).decodeFromByteArray(
                format = HMAC.Key.Format.RAW,
                apiSecret.toByteArray()
            )
            hmacSha256 = key.signatureGenerator().generateSignature(queryString.toByteArray())
        } catch (e: Exception) {
            throw RuntimeException("Failed to calculate hmac-sha256", e)
        }

        return hmacSha256.toHexString()

    }
}
