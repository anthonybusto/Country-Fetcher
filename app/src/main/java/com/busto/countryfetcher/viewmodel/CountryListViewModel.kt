package com.busto.countryfetcher.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.busto.countryfetcher.R
import com.busto.countryfetcher.model.Country
import com.busto.countryfetcher.repository.CountryRepository
import com.busto.countryfetcher.utils.CoroutineScheduler
import com.busto.countryfetcher.utils.Logger
import com.busto.countryfetcher.utils.ResourceResolver
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


//ViewState
sealed class CountryListViewState : Parcelable {
    @Parcelize
    object Loading : CountryListViewState()

    @Parcelize
    data class Content(
        val title: String,
        val countries: @RawValue List<Country> = emptyList()
    ) : CountryListViewState()

    @Parcelize
    data class Error(val t: Throwable) : CountryListViewState()
}


/**
 * Due to specific instructions, we are not using DI
 * Ideally the ViewModel and it's dependencies would be provided via
 * some DI framework like Dagger Hilt
 */
//ViewModel
class CountryListViewModel constructor(
    private val countryRepository: CountryRepository,
    private val resourceResolver: ResourceResolver,
    private val coroutineScheduler: CoroutineScheduler,
    private val logger: Logger,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        private const val KEY_VIEW_STATE = "VIEW_STATE"
    }

    val viewStateFlow = savedStateHandle.getStateFlow<CountryListViewState>(
        KEY_VIEW_STATE,
        CountryListViewState.Loading
    )


    fun start() {
        val savedViewState = savedStateHandle.get<CountryListViewState>(KEY_VIEW_STATE)

        viewModelScope.launch {
            kotlin.runCatching {

                /**
                 * If we already started this ViewModel and set the Content state,
                 * we should not fetch the countries again since the data should be
                 * persisted in the savedStateHandle
                 */
                if (savedViewState !is CountryListViewState.Content) {

                    savedStateHandle[KEY_VIEW_STATE] = CountryListViewState.Loading

                    val countries = withContext(coroutineScheduler.io()) {
                        countryRepository.getCountries()
                    }

                    val contentState = CountryListViewState.Content(
                        title = resourceResolver.getString(R.string.country_list_title),
                        countries = countries
                    )

                    savedStateHandle[KEY_VIEW_STATE] = contentState
                }

            }.onFailure { e ->
                savedStateHandle[KEY_VIEW_STATE] = CountryListViewState.Error(e)
                logger.error(t = e)
            }
        }
    }

}

//ViewModelProviderFactory
class CountryListViewModelFactory constructor(
    private val countryRepository: CountryRepository,
    private val resourceResolver: ResourceResolver,
    private val coroutineScheduler: CoroutineScheduler,
    private val logger: Logger,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountryListViewModel::class.java)) {
            return CountryListViewModel(
                countryRepository,
                resourceResolver,
                coroutineScheduler,
                logger,
                savedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

