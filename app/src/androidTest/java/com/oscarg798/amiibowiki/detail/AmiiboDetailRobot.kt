/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.detail

import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.testutils.isViewWithIdDisplayed
import com.oscarg798.amiibowiki.testutils.isViewWithTextDisplayed
import com.oscarg798.amiibowiki.testutils.utils.TestRobot

class AmiiboDetailRobot : TestRobot {

    override fun isViewDisplayed() {
        isViewWithIdDisplayed(R.id.ivImage)
    }

    fun isAmiiboDataDisplayed() {
        isViewWithTextDisplayed("Mario")
        isViewWithTextDisplayed("Super Mario Bros")
        isViewWithTextDisplayed("Figure")
        isViewWithTextDisplayed("Related Games")
    }
}

private const val CHARACTER_VIEW_INDEX = 1
