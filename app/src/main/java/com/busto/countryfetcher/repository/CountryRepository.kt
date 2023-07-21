package com.busto.countryfetcher.repository

import com.busto.countryfetcher.api.CountryService
import com.busto.countryfetcher.model.Country

class CountryRepository constructor(private val service: CountryService) {
    suspend fun getCountries(): List<Country> {
        return service.getCountries()
    }
}