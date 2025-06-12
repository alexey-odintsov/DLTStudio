package com.alekso.dltstudio.extraction

import java.util.concurrent.atomic.AtomicInteger

fun <T> foreachWithProgressParallelStream(
    collection: Collection<T>,
    onProgressChanged: (Float) -> Unit,
    debounceMs: Long = 32L,
    action: (Int, T) -> Unit
): Long {
    val start = System.currentTimeMillis()
    var prevTs = start
    val totalItems = collection.size
    val processedCount = AtomicInteger(0)

    collection.parallelStream().forEach { message ->
        val currentProgress = processedCount.incrementAndGet()
        val nowTs = System.currentTimeMillis()
        if (nowTs - prevTs > debounceMs || currentProgress == totalItems) {
            prevTs = nowTs
            onProgressChanged(currentProgress.toFloat() / totalItems)
        }
        action(processedCount.get(), message)
    }
    onProgressChanged(1f)
    return System.currentTimeMillis() - start
}