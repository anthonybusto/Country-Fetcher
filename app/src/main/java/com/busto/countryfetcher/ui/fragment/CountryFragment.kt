package com.busto.countryfetcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.busto.countryfetcher.databinding.FragmentCountryBinding
import com.busto.countryfetcher.extensions.launchOnStart
import com.busto.countryfetcher.extensions.viewBinding
import com.busto.countryfetcher.factory.activityViewModel
import com.busto.countryfetcher.ui.adapter.CountryListAdapter
import com.busto.countryfetcher.viewmodel.CountryListViewModel
import com.busto.countryfetcher.viewmodel.CountryListViewState


class CountryFragment : Fragment() {

    private val binding by viewBinding(FragmentCountryBinding::inflate)

    /**
     * Due to specific instructions, we are not using DI
     * Ideally the ViewModel and it's dependencies would be provider via
     * some DI framework like Dagger Hilt
     */
    private val countryViewModel: CountryListViewModel by activityViewModel()

    private val countryListAdapter: CountryListAdapter = CountryListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        with(binding) {
            errorLayout.errorRetryButton.setOnClickListener {
                countryViewModel.start()
            }
            countryRecyclerView.adapter = countryListAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchOnStart {
            countryViewModel.viewStateFlow.collect { state ->
                when (state) {
                    is CountryListViewState.Loading -> renderLoading()
                    is CountryListViewState.Content -> renderContent(state)
                    is CountryListViewState.Error -> renderErrorLayout()
                }
            }
        }

        countryViewModel.start()
    }

    private fun renderLoading() {
        with(binding) {
            loadingProgressBar.visibility = View.VISIBLE
            emptyLayout.root.visibility = View.GONE
            errorLayout.root.visibility = View.GONE
            countryRecyclerView.visibility = View.GONE
        }
    }

    private fun renderContent(state: CountryListViewState.Content) {
        with(binding) {
            toolbar.title = state.title
            loadingProgressBar.visibility = View.GONE
            emptyLayout.root.visibility = View.GONE
            errorLayout.root.visibility = View.GONE
            countryRecyclerView.visibility = View.VISIBLE
        }
        if (state.countries.isEmpty()) {
            renderEmptyLayout()
        } else {
            countryListAdapter.submitList(state.countries)
        }
    }

    private fun renderEmptyLayout() {
        with(binding) {
            emptyLayout.root.visibility = View.VISIBLE
            errorLayout.root.visibility = View.GONE
            countryRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
        }
    }

    private fun renderErrorLayout() {
        with(binding) {
            errorLayout.root.visibility = View.VISIBLE
            emptyLayout.root.visibility = View.GONE
            countryRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
        }
    }
}


