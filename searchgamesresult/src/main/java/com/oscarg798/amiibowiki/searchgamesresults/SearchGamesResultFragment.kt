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

package com.oscarg798.amiibowiki.searchgamesresults

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.extensions.bundle
import com.oscarg798.amiibowiki.core.extensions.startDeepLinkIntent
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.searchgamesresults.adapter.SearchResultAdapter
import com.oscarg798.amiibowiki.searchgamesresults.adapter.SearchResultClickListener
import com.oscarg798.amiibowiki.searchgamesresults.databinding.FragmentSearchResultBinding
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultViewState
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchResultFragment : Fragment(), SearchResultClickListener {

    private val viewModel: SearchGamesResultViewModel by viewModels()

    private lateinit var binding: FragmentSearchResultBinding

    private var gameSearchResultCoverImageView: ImageView? = null

    private val isShownAsGamesRelatedSection: Boolean by bundle(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModelInteractions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newContext = ContextThemeWrapper(requireActivity(), R.style.AppTheme_SearchGames)
        val layoutInflater = inflater.cloneInContext(newContext)
        binding = FragmentSearchResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        val relatedGamesViewsVisibility = if (isShownAsGamesRelatedSection) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvSlideUpIndicator.visibility = relatedGamesViewsVisibility
        binding.tvTitle.visibility = relatedGamesViewsVisibility

        with(binding.rvGamesRelated) {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchResultAdapter(this@SearchResultFragment)
        }
    }

    override fun onResultClicked(
        gameSearchResult: ViewGameSearchResult,
        coverImageView: ImageView
    ) {
        viewModel.onWish(SearchResultWish.ShowGameDetail(gameSearchResult.gameId))
        this.gameSearchResultCoverImageView = coverImageView
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect { state ->
                when (state) {
                    SearchResultViewState.Loading -> showLoading()
                    is SearchResultViewState.ShowingGameResults -> showGameResults(state.results)
                    is SearchResultViewState.ShowingGameDetails -> showGameDetails(
                        state.details.gameId
                    )
                    is SearchResultViewState.Error -> showError(state.error)
                }
            }
        }
    }

    fun search(gameSearchGameQueryParam: GameSearchParam) {
        viewModel.onWish(SearchResultWish.SearchGames(gameSearchGameQueryParam))
    }

    private fun showGameDetails(gameId: Int) {
        hideError()
        startDeepLinkIntent(
            GAME_DETAIL_DEEPLINK,
            gameSearchResultCoverImageView!!,
            Bundle().apply {
                putInt(ARGUMENT_GAME_ID, gameId)
            }
        )
    }

    private fun showGameResults(gameResults: Collection<ViewGameSearchResult>) {
        hideError()
        hideLoading()
        (binding.rvGamesRelated.adapter as SearchResultAdapter).submitList(gameResults.toList())
        binding.lEmptyState.clEmptyState.visibility = if (gameResults.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showLoading() {
        hideError()
        binding.lEmptyState.clEmptyState.visibility = View.GONE

        binding.rvGamesRelated.visibility = View.GONE
        binding.searchResultAnimationList.gameResultShimmerLoadingView.visibility = View.VISIBLE
        binding.searchResultAnimationList.gameResultShimmerLoadingView.startShimmer()
    }

    private fun showError(searchGameFailure: SearchGameFailure) {
        hideLoading()
        with(binding.tvError) {
            text = when (searchGameFailure) {
                is SearchGameFailure.DataSourceNotAvailable,
                is SearchGameFailure.DateSourceError -> context.getString(R.string.data_not_available_error)
                else -> context.getString(R.string.generic_error)
            }
        }
        binding.tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.searchResultAnimationList.gameResultShimmerLoadingView.visibility = View.GONE
        binding.searchResultAnimationList.gameResultShimmerLoadingView.stopShimmer()
        binding.rvGamesRelated.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(shownAsGameRelatedSection: Boolean = true) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION, shownAsGameRelatedSection)
            }
        }
    }
}

private const val ARGUMENT_CURRENT_SEARCH_RESULT_STATE = "ARGUMENT_CURRENT_SEARCH_RESULT_STATE"
private const val SHIMMER_ELEMENTS_COUNT = 10
