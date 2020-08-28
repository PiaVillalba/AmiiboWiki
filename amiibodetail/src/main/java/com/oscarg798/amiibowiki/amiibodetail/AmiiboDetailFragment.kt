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

package com.oscarg798.amiibowiki.amiibodetail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ConstraintSet
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.state
import androidx.compose.runtime.stateFor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.databinding.FragmentAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.di.DaggerAmiiboDetailComponent
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ShowingAmiiboDetailsParams
import com.oscarg798.amiibowiki.amiibodetail.ui.AppTheme
import com.oscarg798.amiibowiki.amiibodetail.ui.purple200
import com.oscarg798.amiibowiki.amiibodetail.ui.white
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.di.entrypoints.AmiiboDetailEntryPoint
import com.oscarg798.amiibowiki.core.di.modules.CoreModule
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.searchgamesresults.SearchResultFragment
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AmiiboDetailFragment : Fragment() {

    @Inject
    lateinit var viewModel: AmiiboDetailViewModel

    private lateinit var binding: FragmentAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmiiboDetailBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val tail = arguments?.getString(ARGUMENT_TAIL, null)
            ?: throw IllegalArgumentException("Tail is required")

        DaggerAmiiboDetailComponent.factory()
            .create(
                tail,
                EntryPointAccessors.fromApplication(
                    requireActivity().application,
                    AmiiboDetailEntryPoint::class.java
                )
            )
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.searchResultFragment,
                SearchResultFragment.newInstance(),
                getString(R.string.search_result_fragment_tag)
            )
            .commit()
        ViewCompat.isNestedScrollingEnabled(binding.searchResultFragment)

        binding.searchResultFragment.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        configureBackDrop()

        viewModel.state.onEach {
            when {
                it.isLoading -> showLoading()
                it.error != null -> showError(it.error)
                it.imageExpanded != null -> showExpandedImages(listOf(it.imageExpanded))
                it.amiiboDetails != null -> showDetail(it.amiiboDetails)
            }
        }.launchIn(lifecycleScope)

        viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)


    }

//    override fun onBackPressed() {
//        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun showError(amiiboDetailFailure: AmiiboDetailFailure) {
        hideLoading()
        Snackbar.make(
            binding.clMain,
            amiiboDetailFailure.message ?: getString(R.string.general_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun configureBackDrop() {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.searchResultFragment)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDetail(
        showingAmiiboDetailsParams: ShowingAmiiboDetailsParams
    ) {
        hideLoading()
        val viewAmiiboDetails = showingAmiiboDetailsParams.amiiboDetails
//        supportActionBar?.title = viewAmiiboDetails.name

//        with(binding) {
//            ivImage.setImage(viewAmiiboDetails.imageUrl)
//            tvGameCharacter.setText(viewAmiiboDetails.character)
//            tvSerie.setText(viewAmiiboDetails.gameSeries)
//            tvType.setText(viewAmiiboDetails.type)
//
//            ivImage.setOnClickListener {
//
//            }
//        }

        binding.composeView.setContent {
            MyView(showingAmiiboDetailsParams.amiiboDetails) {
                viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(it))
            }
        }

        if (showingAmiiboDetailsParams.isRelatedGamesSectionEnabled) {
            showSearchResultFragment(viewAmiiboDetails.id)
        } else {
            hideSearchResultFragment()
        }
    }

    private fun showSearchResultFragment(amiiboId: String) {
        binding.searchResultFragment.visibility = View.VISIBLE
        val searchResultFragment = getSearchResultsFragment() ?: return
        searchResultFragment.search(GameSearchParam.AmiiboGameSearchParam(amiiboId))
    }

    private fun hideSearchResultFragment() {
        binding.searchResultFragment.visibility = View.GONE
    }

    private fun showLoading() {
//        with(binding) {
//            shimmer.root.visibility = View.VISIBLE
//            shimmer.shimmerViewContainer.startShimmer()
//            tvSerieTitle.visibility = View.GONE
//            tvTypeTitle.visibility = View.GONE
//        }
    }

    private fun hideLoading() {
//        with(binding) {
//            shimmer.root.visibility = View.GONE
//            shimmer.shimmerViewContainer.stopShimmer()
//            tvSerieTitle.visibility = View.VISIBLE
//            tvTypeTitle.visibility = View.VISIBLE
//        }
    }

    private fun getSearchResultsFragment(): SearchResultFragment? {
        return childFragmentManager.findFragmentByTag(getString(R.string.search_result_fragment_tag)) as? SearchResultFragment
    }
}

sealed class Tag {
    object AmiiboNameTag : Tag()
}

@Composable
fun Cover(url: String, onImageClicked: (imageUrl: String) -> Unit = {}) {
    val image: MutableState<Bitmap?> = state { null }

    val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            image.value = bitmap
        }

        override fun onBitmapFailed() {

        }
    }
    CoreModule.picasso.load(url).into(target)

    image.value?.let {
        Image(
            asset = it.asImageAsset(),
            modifier = Modifier.width(250.dp)
                .padding(top = 16.dp, bottom = 16.dp)
                .height(250.dp)
                .gravity(Alignment.CenterHorizontally)
                .clickable(onClick = {
                    onImageClicked(url)
                })
        )
    }
}

@Composable
fun InformationText(label: String, value: String) {
    Row(modifier = Modifier.gravity(align = Alignment.Start).padding(top = 8.dp)) {
        Text(
            text = label,
            modifier = Modifier.wrapContentHeight()
                .wrapContentWidth(),
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        )
        Text(
            text = value,
            modifier = Modifier.wrapContentHeight()
                .wrapContentWidth(),
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun MyView(viewAmiibo: ViewAmiiboDetails, onImageClicked: (imageUrl: String) -> Unit) {
    AppTheme {
        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    viewAmiibo.name,
                    style = TextStyle(fontWeight = FontWeight.Black, fontSize = 24.sp),
                    modifier = Modifier.wrapContentHeight()
                        .wrapContentWidth()
                        .gravity(align = Alignment.CenterHorizontally)
                )
                Cover(url = viewAmiibo.imageUrl, onImageClicked = onImageClicked)
                InformationText(label = "Game Serie: ", value = viewAmiibo.gameSeries)
                InformationText(label = "Character: ", value = viewAmiibo.character)
                InformationText(label = "Type: ", value = viewAmiibo.type)
            }
        }
    }
}


private const val NO_ELEVATION = 0f
