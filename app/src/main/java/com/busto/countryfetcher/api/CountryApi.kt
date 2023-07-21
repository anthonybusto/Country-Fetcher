package com.busto.countryfetcher.api

import com.busto.countryfetcher.model.Country
import retrofit2.http.GET

interface CountryApi {
    @GET("peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
    suspend fun getCountries(): List<Country>
}