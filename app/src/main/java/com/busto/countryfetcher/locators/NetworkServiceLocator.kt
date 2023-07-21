package com.busto.countryfetcher.locators

import com.busto.countryfetcher.api.CountryApi
import com.busto.countryfetcher.api.CountryService
import com.busto.countryfetcher.repository.CountryRepository
import com.busto.countryfetcher.utils.CoroutineScheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkServiceLocator {

    private const val BASE_URL = "https://gist.githubusercontent.com/"

    private fun providesRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(providesOkHttpClient())
        .build()

    private fun providesOkHttpClient() = OkHttpClient.Builder()
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun providesCoroutineScheduler() = CoroutineScheduler()

    private fun providesCountryService(): CountryService {
        return CountryService(
            providesRetrofit().create(CountryApi::class.java)
        )
    }

    fun providesCountryRepository(): CountryRepository {
        return CountryRepository(providesCountryService())
    }

}