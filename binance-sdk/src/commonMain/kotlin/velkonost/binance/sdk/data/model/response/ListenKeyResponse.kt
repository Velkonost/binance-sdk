package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class ListenKeyResponse(
    val listenKey: String
)