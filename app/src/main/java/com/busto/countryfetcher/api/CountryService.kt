package com.busto.countryfetcher.api

import com.busto.countryfetcher.model.Country

class CountryService constructor(private val api: CountryApi) {
    suspend fun getCountries(): List<Country> = api.getCountries()
}