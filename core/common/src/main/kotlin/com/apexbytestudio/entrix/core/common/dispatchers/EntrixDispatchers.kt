package com.apexbytestudio.entrix.core.common.dispatchers

/**
 * Identifies the application's [kotlinx.coroutines.CoroutineDispatcher]s so they can be
 * injected (and swapped for test dispatchers) instead of referencing `Dispatchers.*`
 * directly inside data/domain code.
 */
enum class EntrixDispatcher {
    Default,
    IO,
}
