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

package com.oscarg798.amiibowiki.amiibodetail.ui

import androidx.compose.ui.graphics.Color

val silver_chali = "#AAAAAA".toColor()
val white = "#ffffff".toColor()

val mine_shaft_700 = "#616161".toColor()
val mine_shaft_800 = "#424242".toColor()
val mine_shaft_900 = "#212121".toColor()

val manatee = "#919099".toColor()
val steel_gray = "#1A1824".toColor()
val alto = "#E0E0E0".toColor()

val white_lilac = "#F8F9FC".toColor()
val royal_blue = "#4363F6".toColor()

val shark = "#25272A".toColor()
val shark_800 = "#1B1C1F".toColor()

val alabaster_50 = "#4363f6".toColor()
val alabaster_100 = "#F5F5F5".toColor()
val alabaster_200 = "#EEEEEE".toColor()

val cinnabar = "#EB322C".toColor()
val atlantis = "#A7C73E".toColor()
val picton_blue = "#4AAAE6".toColor()
val viridian = "#3A8365".toColor()
val tulip_tree = "#F1AF3D".toColor()
val cerise = "#D63080".toColor()

private fun String.toColor() = Color(android.graphics.Color.parseColor(this))
