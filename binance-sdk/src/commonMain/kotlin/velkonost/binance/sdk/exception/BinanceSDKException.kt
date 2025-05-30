package velkonost.binance.sdk.exception

/**
 * Base exception class for all Binance SDK related exceptions.
 * All specific exceptions in the SDK should extend this class to maintain a consistent
 * exception hierarchy and error handling approach.
 *
 * @property message A detailed description of the error that occurred
 */
open class BinanceSDKException(override val message: String) : Exception(message)

/**
 * Exception thrown when attempting to use the Binance SDK before initialization.
 * The SDK must be initialized with valid API credentials using [BinanceSDK.setup] before
 * any operations can be performed.
 *
 * This exception is thrown when:
 * 1. No API credentials have been provided
 * 2. The SDK's setup method hasn't been called
 * 3. The initialization process failed
 */
object BinanceSDKNotInitializedException: BinanceSDKException("binance client is not initialized")
