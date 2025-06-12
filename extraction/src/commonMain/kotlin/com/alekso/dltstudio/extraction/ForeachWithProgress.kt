package com.alekso.dltstudio.extraction

inline fun <T> forEachWithProgress(
    collection: Collection<T>,
    onProgressChanged: (Float) -> Unit,
    debounceMs: Long = 32L,
    action: (Int, T) -> Unit
): Long {
    val start = System.currentTimeMillis()
    var prevTs = start
    val totalItems = collection.size

    collection.forEachIndexed { index, item ->
        action(index, item)
        val nowTs = System.currentTimeMillis()
        if (nowTs - prevTs > debounceMs) {
            prevTs = nowTs
            onProgressChanged(index.toFloat() / totalItems)
        }
    }
    onProgressChanged(1f)
    return System.currentTimeMillis() - start
}
