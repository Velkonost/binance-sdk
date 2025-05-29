package velkonost.binance.sdk.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import velkonost.binance.sdk.data.datasource.DataSource

internal abstract class Repository {

    protected abstract val dataSource: DataSource
    protected val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())
}