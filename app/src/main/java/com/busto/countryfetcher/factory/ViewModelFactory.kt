package com.busto.countryfetcher.factory

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.busto.countryfetcher.locators.CountryServiceLocator
import com.busto.countryfetcher.locators.LoggingServiceLocator
import com.busto.countryfetcher.locators.NetworkServiceLocator
import com.busto.countryfetcher.locators.ResourcesServiceLocator
import com.busto.countryfetcher.viewmodel.CountryListViewModel
import com.busto.countryfetcher.viewmodel.CountryListViewModelFactory


inline fun <reified VM : ViewModel> Fragment.activityViewModel(): Lazy<VM> = activityViewModels {
    ViewModelFactory<VM>().createViewModelProviderFactory(VM::class.java)
}


class ViewModelFactory<VM : ViewModel> {

    fun createViewModelProviderFactory(modelClass: Class<VM>): ViewModelProvider.Factory {
        return when {
            modelClass.isAssignableFrom(CountryListViewModel::class.java) -> {
                CountryListViewModelFactory(
                    NetworkServiceLocator.providesCountryRepository(),
                    ResourcesServiceLocator.providesResourceResolver(),
                    NetworkServiceLocator.providesCoroutineScheduler(),
                    LoggingServiceLocator.providesLogger(),
                    CountryServiceLocator.providesCountryMapper(),
                    SavedStateHandle()
                )
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
