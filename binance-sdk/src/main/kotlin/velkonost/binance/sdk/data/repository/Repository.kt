package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import velkonost.binance.sdk.data.datasource.DataSource

internal abstract class Repository {

    protected abstract val dataSource: DataSource
    protected val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
}