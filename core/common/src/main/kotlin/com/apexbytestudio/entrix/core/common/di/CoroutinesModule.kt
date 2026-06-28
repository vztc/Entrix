package com.apexbytestudio.entrix.core.common.di

import com.apexbytestudio.entrix.core.common.dispatchers.EntrixDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Exposes the app's dispatchers as injectable, qualified singletons so background work can be
 * redirected to a test dispatcher instead of referencing [Dispatchers] directly.
 */
val coroutinesModule = module {
    single<CoroutineDispatcher>(named(EntrixDispatcher.IO)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(EntrixDispatcher.Default)) { Dispatchers.Default }
}
