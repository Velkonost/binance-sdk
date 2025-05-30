package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

/**
 * Represents the response from the Binance API when requesting a listen key.
 * A listen key is required for establishing and maintaining a user data stream
 * through WebSocket connections.
 *
 * @property listenKey The unique key used to authenticate and maintain the user data stream
 */
@Serializable
internal data class ListenKeyResponse(
    val listenKey: String
)