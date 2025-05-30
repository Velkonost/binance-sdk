package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an error response from the Binance API.
 * This model is used when an API request fails and returns an error.
 *
 * @property code The error code returned by the API
 * @property message A human-readable description of the error
 */
@Serializable
internal data class ErrorResponse(
    val code: Int,
    @SerialName("msg")
    val message: String
)