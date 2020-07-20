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

package com.oscarg798.amiibowiki.loggerdecoratorprocessor.functioncreator

import com.oscarg798.amiibowiki.logger.events.ScreenViewEvent
import com.oscarg798.amiibowiki.logger.events.WidgetClickedEvent
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.MethodDecorator
import com.oscarg798.amiibowiki.loggerdecoratorprocessor.builder.ScreenShownMethodDecorator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class ScreenShownFunctionCreator : AbstractFunctionCreator<ScreenShownMethodDecorator>() {

    override fun isApplicable(methodDecorator: MethodDecorator): Boolean =
        methodDecorator is ScreenShownMethodDecorator

    override fun getMethodStatement(methodDecorator: ScreenShownMethodDecorator): String {
        return if (methodDecorator.propertiesName == null) {
            "logger.log(ScreenViewEvent(\"${methodDecorator.screenShownName}\"))"
        } else {
            "logger.log(ScreenViewEvent(\"${methodDecorator.screenShownName}\", ${methodDecorator.propertiesName}))"
        }
    }

    override fun addEventImportToFileSpecBuilder(fileSpecBuilder: FileSpec.Builder) {
        fileSpecBuilder.addImport(
            ScreenViewEvent::class.java.`package`.name,
            ScreenViewEvent::class.java.simpleName
        )
    }
}