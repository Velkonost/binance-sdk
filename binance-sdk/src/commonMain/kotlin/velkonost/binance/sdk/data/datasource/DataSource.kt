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

/**
 * Abstract base class for all data sources in the Binance SDK.
 * This class provides common functionality for making authenticated requests to the Binance API,
 * including request signing and response handling.
 *
 * @property apiKey The API key used for authentication
 * @property apiSecret The secret key used for signing requests
 */
internal abstract class DataSource(
    private val apiKey: String,
    private val apiSecret: String
) : KoinComponent {

    /**
     * The HTTP client instance used for making API requests.
     * This must be implemented by concrete data source classes.
     */
    abstract val httpClient: HttpClient

    /**
     * Generates the required headers for Binance API requests.
     * These headers include the API key and a user agent string.
     *
     * @return A list of header key-value pairs
     */
    protected fun getHeaders() = listOf(
        Pair(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
        ),
        Pair("X-MBX-APIKEY", apiKey)
    )

    /**
     * Maps an HTTP response to a [ResultState] object.
     * This method handles both successful responses (status code 200-299) and errors.
     *
     * @param T The type of data expected in the response
     * @return A [ResultState] containing either the parsed response data or error information
     */
    protected suspend inline fun <reified T> HttpResponse.mapToState(): ResultState<T> =
        if (status.value in 200..299) {
            ResultState.Success(body<T>())
        } else {
            ResultState.Failure(
                code = status.value,
                message = body()
            )
        }

    /**
     * Makes an authenticated GET request to the Binance API.
     * This method automatically adds required parameters like timestamp and signature.
     *
     * @param path The API endpoint path
     * @param parameters Additional query parameters for the request
     * @return An [HttpResponse] object containing the API response
     */
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

    /**
     * Generates a signature for authenticating Binance API requests.
     * The signature is created using HMAC SHA256 with the API secret.
     *
     * @param parameters The query parameters to be signed
     * @return A hexadecimal string representing the signature
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun generateSignature(parameters: List<Pair<String, String>>): String {
        val queryString = parameters
            .sortedBy { it.first }
            .joinToString("&") { "${it.first}=${it.second}" }

        val hmac = CryptographyProvider.hmac(HMAC.SHA256)
        val key = hmac.key(apiSecret.encodeToByteArray())
        val signature = hmac.sign(key, queryString.encodeToByteArray())

        return signature.toHexString()
    }
}
