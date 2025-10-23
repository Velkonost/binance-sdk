package velkonost.binance.sdk

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import velkonost.binance.sdk.client.*
import velkonost.binance.sdk.client.di.BinanceSDKServiceModule
import velkonost.binance.sdk.data.di.BinanceSDKDataModule
import velkonost.binance.sdk.exception.ConfigurationException
import velkonost.binance.sdk.model.FuturesBalance
import velkonost.binance.sdk.model.FuturesOpenPosition
import velkonost.binance.sdk.network.di.BinanceSDKNetworkModule

/**
 * The main entry point for the Binance SDK.
 * This object provides a simplified interface for interacting with the Binance API,
 * supporting both REST and WebSocket operations for spot and futures trading.
 */
class BinanceSDK private constructor() : KoinComponent {

    companion object {
        private var instance: BinanceSDK? = null

        fun initialize(config: BinanceConfig): BinanceSDK {
            if (instance != null) {
                throw ConfigurationException("SDK is already initialized")
            }

            val sdk = BinanceSDK()
            sdk.initializeInternal(config)
            instance = sdk
            return sdk
        }

        fun getInstance(): BinanceSDK {
            return instance ?: throw ConfigurationException("SDK is not initialized. Call initialize() first.")
        }

        fun setup(apiKey: String, apiSecret: String): BinanceSDK {
            val config = BinanceConfigBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build()

            return initialize(config)
        }
    }

    private lateinit var client: Client

    private fun initializeInternal(config: BinanceConfig) {
        loadKoinModules(
            listOf(
                BinanceSDKNetworkModule,
                BinanceSDKDataModule(config.apiKey, config.apiSecret),
                BinanceSDKServiceModule
            )
        )
        client = get()
    }

    val general: GeneralService get() = client.general
    val spot: SpotService get() = client.spot
    val futures: FuturesService get() = client.futures
    val socket: SocketService get() = client.socket
    val account: AccountService get() = client.account

    fun isInitialized(): Boolean = ::client.isInitialized


}

suspend fun BinanceSDK.ping(): BinanceResult<Boolean> = general.ping()
suspend fun BinanceSDK.getFuturesBalance(asset: String = "USDT"): BinanceResult<FuturesBalance> =
    futures.getBalance(asset)

suspend fun BinanceSDK.getOpenPositions(): BinanceResult<List<FuturesOpenPosition>> = futures.getOpenPositions()
suspend fun BinanceSDK.getSymbols(): BinanceResult<List<String>> = futures.getSymbols()


fun binanceConfig(block: BinanceConfigBuilder.() -> Unit): BinanceConfig {
    return BinanceConfigBuilder().apply(block).build()
}
