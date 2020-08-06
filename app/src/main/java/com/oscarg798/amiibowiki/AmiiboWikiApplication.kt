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

package com.oscarg798.amiibowiki

import android.app.Application
import com.oscarg798.amiibowiki.core.di.CoreComponent
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.di.DaggerCoreComponent
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.models.Flavor
import com.oscarg798.flagly.developeroptions.FeatureHandleResourceProvider
import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlagProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


class AmiiboWikiApplication : Application(), CoreComponentProvider,
    FeatureHandleResourceProvider {

    private lateinit var coreComponent: CoreComponent

    override fun provideCoreComponent(): CoreComponent {
        if (!::coreComponent.isInitialized) {
            coreComponent = DaggerCoreComponent.factory().create(
                this, Config(
                    BuildConfig.BASE_AMIIBO_API_URL,
                    BuildConfig.BASE_GAME_API_URL,
                    when{
                        BuildConfig.DEBUG -> Flavor.Debug
                        BuildConfig.ALPHA -> Flavor.Alpha
                        else -> Flavor.Release
                    },
                    BuildConfig.GOOGLE_API_KEY,
                    BuildConfig.GAME_API_KEY
                )
            )
        }

        return coreComponent
    }

    override fun getFeatureFlagProvider(): FeatureFlagProvider =
        coreComponent.provideAmiiboWikiFeatureFlagHandler() as FeatureFlagProvider

    override fun getLocalFeatureflagHandler(): DynamicFeatureFlagHandler =
        coreComponent.provideDynamicFeatureFlag()

    override fun getRemoteFeatureFlagHandler(): FeatureFlagHandler =
        coreComponent.provideRemoteFeatureFlagHandler()
}
