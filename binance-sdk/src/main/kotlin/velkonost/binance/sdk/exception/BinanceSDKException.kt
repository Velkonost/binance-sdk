package velkonost.binance.sdk.exception

open class BinanceSDKException(override val message: String) : Exception(message)
object BinanceSDKNotInitializedException: BinanceSDKException("binance client is not initialized")