package com.busto.countryfetcher

import android.app.Application
import com.busto.countryfetcher.locators.ResourcesServiceLocator

class CountryFetcherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ResourcesServiceLocator.register(this)
    }

}