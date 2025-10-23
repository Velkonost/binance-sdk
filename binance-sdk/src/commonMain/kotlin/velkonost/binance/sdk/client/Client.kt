package velkonost.binance.sdk.client

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The core implementation class of the Binance SDK.
 * This class handles all direct interactions with the Binance API, including REST and WebSocket operations.
 * It manages authentication, request signing, and response handling for both spot and futures trading.
 */
internal class Client() : KoinComponent {
    internal val general: GeneralService by inject<GeneralService>()
    internal val futures: FuturesService by inject<FuturesService>()
    internal val socket: SocketService by inject<SocketService>()
    internal val account: AccountService by inject<AccountService>()
    internal val spot: SpotService by inject<SpotService>()
}