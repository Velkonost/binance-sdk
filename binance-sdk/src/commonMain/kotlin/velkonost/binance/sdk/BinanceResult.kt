package velkonost.binance.sdk

import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.exception.ValidationException

sealed class BinanceResult<out T> {
    data class Success<out T>(val data: T) : BinanceResult<T>()
    data class Failure(val exception: BinanceSDKException) : BinanceResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): BinanceResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> Failure(exception)
    }

    inline fun <R> flatMap(transform: (T) -> BinanceResult<R>): BinanceResult<R> = when (this) {
        is Success -> transform(data)
        is Failure -> Failure(exception)
    }

    inline fun onSuccess(action: (T) -> Unit): BinanceResult<T> = apply {
        if (this is Success) action(data)
    }

    inline fun onFailure(action: (BinanceSDKException) -> Unit): BinanceResult<T> = apply {
        if (this is Failure) action(exception)
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw exception
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }
}

internal inline fun <reified T> T.asResult() = BinanceResult.Success(this)

internal object BinanceValidator {
    fun validateApiKey(apiKey: String): BinanceResult<String> {
        return when {
            apiKey.isBlank() -> BinanceResult.Failure(ValidationException("API key cannot be blank"))
            apiKey.length < 10 -> BinanceResult.Failure(ValidationException("API key is too short"))
            else -> BinanceResult.Success(apiKey)
        }
    }

    fun validateApiSecret(apiSecret: String): BinanceResult<String> {
        return when {
            apiSecret.isBlank() -> BinanceResult.Failure(ValidationException("API secret cannot be blank"))
            apiSecret.length < 10 -> BinanceResult.Failure(ValidationException("API secret is too short"))
            else -> BinanceResult.Success(apiSecret)
        }
    }

    fun validateSymbol(symbol: String): BinanceResult<String> {
        return when {
            symbol.isBlank() -> BinanceResult.Failure(ValidationException("Symbol cannot be blank"))
            !symbol.matches(Regex("[A-Z0-9]+")) -> BinanceResult.Failure(ValidationException("Invalid symbol format"))
            else -> BinanceResult.Success(symbol.uppercase())
        }
    }

    fun validateAsset(asset: String): BinanceResult<String> {
        return when {
            asset.isBlank() -> BinanceResult.Failure(ValidationException("Asset cannot be blank"))
            !asset.matches(Regex("[A-Z0-9]+")) -> BinanceResult.Failure(ValidationException("Invalid asset format"))
            else -> BinanceResult.Success(asset.uppercase())
        }
    }
}