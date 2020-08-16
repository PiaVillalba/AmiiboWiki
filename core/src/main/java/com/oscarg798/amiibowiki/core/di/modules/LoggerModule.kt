/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.core.di.modules

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.oscarg798.amiibowiki.core.logger.FirebaseLogger
import com.oscarg798.amiibowiki.logger.requestprocessor.DefaultRequestProcessor
import com.oscarg798.amiibowiki.logger.sources.FirebaseSource
import com.oscarg798.lomeno.interceptor.NetworkLoggerInterceptor
import com.oscarg798.lomeno.logger.Logger
import com.oscarg798.lomeno.logger.Lomeno
import com.oscarg798.lomeno.mapper.NetworkLogEventMapperImpl
import com.oscarg798.lomeno.sources.NetworkLogSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import okhttp3.Interceptor

@Module
@InstallIn(ApplicationComponent::class)
object LoggerModule {

    @Singleton
    @Provides
    fun provideFirebaseAnlytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Singleton
    @Provides
    fun provideLogger(firebaseLogger: FirebaseLogger): Logger =
        Lomeno(mapOf(FirebaseSource to firebaseLogger, NetworkLogSource to firebaseLogger))

    @Singleton
    @Provides
    fun provideNetworkLoggerInterceptor(logger: Logger): Interceptor =
        NetworkLoggerInterceptor(
            logger, NetworkLogEventMapperImpl(),
            DefaultRequestProcessor()
        )
}