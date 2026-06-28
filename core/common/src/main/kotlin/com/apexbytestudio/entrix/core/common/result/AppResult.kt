package com.apexbytestudio.entrix.core.common.result

/**
 * A minimal result wrapper for operations that can fail in a way the UI must react to
 * (e.g. geofence registration, persistence). Domain/data return this instead of throwing,
 * keeping error handling explicit and exhaustive at call sites.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: Throwable) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T> appResultCatching(block: () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        AppResult.Failure(t)
    }
