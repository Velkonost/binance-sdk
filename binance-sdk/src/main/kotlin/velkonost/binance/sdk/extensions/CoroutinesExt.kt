package velkonost.binance.sdk.extensions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal fun CoroutineScope.launchCatching(
    catch: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(
    context = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        catch?.invoke(throwable)
    },
    block = block
)