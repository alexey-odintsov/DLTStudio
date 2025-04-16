package com.alekso.dltstudio.uicomponents

inline fun <T> forEachWithProgress(
    collection: Collection<T>,
    onProgressChanged: (Float) -> Unit,
    debounceMs: Long = 30L,
    action: (Int, T) -> Unit
): Long {
    val start = System.currentTimeMillis()
    var prevTs = start
    val total = collection.size
    collection.forEachIndexed { index, item ->
        action(index, item)
        val nowTs = System.currentTimeMillis()
        if (nowTs - prevTs > debounceMs) {
            prevTs = nowTs
            onProgressChanged(index.toFloat() / total)
        }
    }
    onProgressChanged(1f)
    return System.currentTimeMillis() - start
}
