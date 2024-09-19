package velkonost.binance.sdk.network

import kotlinx.coroutines.CompletableDeferred
import velkonost.binance.sdk.data.model.response.ErrorResponse
import velkonost.binance.sdk.exception.BinanceSDKException
import velkonost.binance.sdk.exception.BinanceSDKNotInitializedException

internal sealed class ResultState<out T> {
    data class Success<out T : Any?>(val data: T) : ResultState<T>()
    data class Failure(
        val throwable: Throwable? = null,
        val code: Int? = null,
        val message: ErrorResponse? = null,
    ) : ResultState<Nothing>()

    data object Loading : ResultState<Nothing>()
}

internal inline fun <T : Any?> ResultState<T>.isLoading(crossinline action: (isLoading: Boolean) -> Unit): ResultState<T> {
    if (this is ResultState.Loading) action(true) else action(false)
    return this
}

internal inline fun <T : Any?> ResultState<T>.onSuccess(crossinline action: (T) -> Unit): ResultState<T> {
    if (this is ResultState.Success) action(this.data)
    return this
}

internal inline fun <T : Any?> ResultState<T>.onFailure(crossinline action: (throwable: Throwable?, error: ErrorResponse?) -> Unit): ResultState<T> {
    if (this is ResultState.Failure) action(this.throwable, this.message)
    return this
}

internal fun <T : Any> CompletableDeferred<T>.handleError(throwable: Throwable?, error: ErrorResponse? = null) {
    val exception = when {
        error != null -> BinanceSDKException(error.message)
        throwable != null -> throwable
        else -> BinanceSDKNotInitializedException
    }
    completeExceptionally(exception)
}