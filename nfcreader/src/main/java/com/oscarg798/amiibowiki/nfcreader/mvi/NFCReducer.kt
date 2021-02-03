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

package com.oscarg798.amiibowiki.nfcreader.mvi

import com.oscarg798.amiibowiki.core.mvi.Reducer
import javax.inject.Inject

class NFCReducer @Inject constructor() : Reducer<NFCReaderResult, NFCReaderViewState> {

    override suspend fun reduce(
        state: NFCReaderViewState,
        from: NFCReaderResult
    ): NFCReaderViewState {
        return when (from) {
            is NFCReaderResult.Reading -> NFCReaderViewState.Loading
            is NFCReaderResult.AdapterReady -> NFCReaderViewState.AdapterStatusFound(NFCReaderViewState.AdapterStatus.AdapterAvailable)
            is NFCReaderResult.AdapterDisabled,
            is NFCReaderResult.AdapterStoped -> NFCReaderViewState.AdapterStatusFound(NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped)
            is NFCReaderResult.ReadSuccessful -> NFCReaderViewState.ShowingAmiibo(from.amiiboIdentifier)
            is NFCReaderResult.Error -> NFCReaderViewState.Error(from.error)
        }
    }
}
