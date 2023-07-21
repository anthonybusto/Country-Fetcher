package com.busto.countryfetcher.mapper

import com.busto.countryfetcher.model.Country
import com.busto.countryfetcher.ui.renderables.CountryRenderable

class CountryMapper {

    fun mapCountryToRenderable(countries: List<Country>): List<CountryRenderable> {
        return countries.map {
            CountryRenderable(
                capital = it.capital,
                code = it.code,
                name = it.name,
                region = it.region,
            )
        }
    }
}