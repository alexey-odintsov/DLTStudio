package com.alekso.dltstudio.extraction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> foreachWithProgressParallelFlow(
    collection: Collection<T>,
    onProgressChanged: (Float) -> Unit,
    debounceMs: Long = 32L,
    concurrencyLevel: Int = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1),
    action: (Int, T) -> Unit
): Long {
    val start = System.currentTimeMillis()
    var prevTs = start
    val totalItems = collection.size
    val processedCount = AtomicInteger(0)

    collection.asFlow()
        .onEach {
            val currentProgress = processedCount.incrementAndGet()
            val nowTs = System.currentTimeMillis()
            if (nowTs - prevTs > debounceMs || currentProgress == totalItems) {
                prevTs = nowTs
                onProgressChanged(currentProgress.toFloat() / totalItems)
            }
        }
        .flatMapMerge(concurrency = concurrencyLevel) { message ->
            flow {
                emit(action(processedCount.get(), message))
            }

        }
        .filterNotNull()
        .flowOn(Dispatchers.Default)
        .toList()
    onProgressChanged(1f)
    return System.currentTimeMillis() - start
}