package com.busto.countryfetcher.locators

import com.busto.countryfetcher.mapper.CountryMapper

object CountryServiceLocator {
    fun providesCountryMapper() = CountryMapper()
}